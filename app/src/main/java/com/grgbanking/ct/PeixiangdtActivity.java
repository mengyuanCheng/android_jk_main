package com.grgbanking.ct;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.grgbanking.ct.entity.EmployeeName;
import com.grgbanking.ct.http.ResultInfo;
import com.grgbanking.ct.rfid.UfhData;
import com.grgbanking.ct.rfid.UfhData.UhfGetData;
import com.grgbanking.ct.utils.HttpUtils;
import com.grgbanking.ct.utils.ScanActivity;
import com.grgbanking.ct.utils.WaitDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/7/12.
 * <p>
 * 用于对配箱的操作：包括扫描出装箱人员，扫描20捆钱进行装箱
 * </p>
 */
public class PeixiangdtActivity extends Activity implements View.OnClickListener {

    private EmployeeName bankEmployee1;                //用来显示工牌扫描出的结果在后台匹配的人员信息
    private EmployeeName bankEmployee2;
    private Context mContext;
    private Button pxDetailBack;                       //返回按钮
    private Button pxDetailScanPerson;                 //扫描人员信息
    private Button pxDetailScanPeiXiang;               //扫描配箱
    private Button pxDetailScanMoney;                  //扫描钱
    private Button pxDetailCommit;                     //提交数据
    private Spinner pxSpinner;                          //选择面额
    private TextView pxDetailPersonName1;               //配箱人员姓名
    private TextView pxDetailPersonName2;
    private TextView pxDetailBoxName;                  //配箱号
    private ListView pxDetailListView;                 //listView 用来保存20捆钱的信息

    private ArrayList<String> mArrayList = new ArrayList<>();    //钱捆信息列表
    private ArrayAdapter arrayAdapter;
    private String pxName; //px号   需上传
    private String moneyAmount; //面额  需上传

    SimpleAdapter listItemAdapter;
    ArrayList<HashMap<String, Object>> listItem;
    private Map<String, Integer> data;
    private Timer timer;
    private boolean isCanceled = true;
    private boolean Scanflag = false;                  //false 表示没有在扫描
    private static final int MSG_UPDATE_EMPLOYEE_NAME = 0;
    private static final int MSG_GET_NAME = 10;       //查询名字what 标记
    private static final int MSG_DATA_COMMIT = 20;   //提交数据what 标记
    private static final int SCAN_INTERVAL = 10;  //扫描间隔

    private String requestContent;
    private WaitDialogFragment waitDialogFragment;  //提交数据等待页面
    private Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.peixiangdetail);
        mContext = PeixiangdtActivity.this;
        findViewById();
        setOnClickListener();
        arrayAdapter = new ArrayAdapter(this, R.layout.simple_listview_item,
                R.id.listView_item_textView, mArrayList);
        pxDetailListView.setAdapter(arrayAdapter);
        pxDetailListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("删除所选钱捆?")
                        .setNegativeButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mArrayList.remove(position);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        })
                        .setPositiveButton("取消",null)
                        .show();
                return false;
            }
        });
    }

    private void findViewById() {
        pxDetailBack = (Button) findViewById(R.id.px_detail_back);
        pxDetailCommit = (Button) findViewById(R.id.px_detail_commit);
        pxDetailScanPerson = (Button) findViewById(R.id.px_detail_person_scan);
        pxDetailScanPeiXiang = (Button) findViewById(R.id.px_detail_scan_peixiang);
        pxDetailScanMoney = (Button) findViewById(R.id.px_detail_money_scan);
        pxDetailPersonName1 = (TextView) findViewById(R.id.px_detail_show_name1);
        pxDetailPersonName2 = (TextView) findViewById(R.id.px_detail_show_name2);
        pxDetailBoxName = (TextView) findViewById(R.id.px_detail_show_box_name);
        pxDetailListView = (ListView) findViewById(R.id.px_detail_listView);
        pxSpinner = (Spinner) findViewById(R.id.px_detail_spinner);
    }

    private void setOnClickListener() {
        /** 返回peixiangActivity */
        pxDetailBack.setOnClickListener(this);
        /** 扫描工牌操作  */
        pxDetailScanPerson.setOnClickListener(this);
        pxDetailScanPeiXiang.setOnClickListener(this);
        /** 扫描钱捆操作 */
        pxDetailScanMoney.setOnClickListener(this);
        /** 提交数据操作 */
        pxDetailCommit.setOnClickListener(this);
        /* 选择面额操作*/
        pxSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                moneyAmount = parent.getItemAtPosition(position).toString();
                Log.i("选择的面额是----->", moneyAmount);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.px_detail_back:
                backListPage();
                break;
            case R.id.px_detail_person_scan:
                if (!UfhData.isDeviceOpen()) {
                    connectDevice();
                    if (!UfhData.isDeviceOpen())
                        break;
                }
                if (pxDetailScanPerson.getText().toString().equals("添加人员")) {
                    startScan();
                } else if (pxDetailScanPerson.getText().toString().equals("停止扫描")) {
                    cancelScan();
                }
                break;
            //TODO 扫描配箱
            case R.id.px_detail_scan_peixiang:
                scanPeiXiang();
                break;
            case R.id.px_detail_money_scan:
                Intent intent = new Intent(PeixiangdtActivity.this, ScanActivity.class);
                intent.setFlags(ScanActivity.FLAG_PX_DETAIL);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list",mArrayList);
                intent.putExtra("bundle",bundle);
                startActivityForResult(intent, ScanActivity.FLAG_PX_DETAIL);
                if (UfhData.isDeviceOpen())
                    UhfGetData.CloseUhf(); //跳转页面前结束连接
                break;
            //TODO 提交数据到服务器
            case R.id.px_detail_commit:
                commitData();
                break;
            default:
                break;
        }
    }


    private int tty_speed = 57600;
    private byte addr = (byte) 0xff;

    /**
     * 连接设备
     */
    private void connectDevice() {
        int result = UhfGetData.OpenUhf(tty_speed, addr, 4, 1, null);
        if (result == 0) {
            UfhData.UhfGetData.GetUhfInfo();
            Toast.makeText(PeixiangdtActivity.this, "连接设备成功！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(PeixiangdtActivity.this, "连接设备失败，请关闭程序重新登录", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 启动扫描
     */
    private void startScan() {
        try {
            if (!UfhData.isDeviceOpen()) {
                Toast.makeText(this, "连接设备失败，请关闭程序重新登录", Toast.LENGTH_LONG).show();
                return;
            }
            Log.e("startScan----->", "1");
            if (timer == null) {
                //声音开关初始化
                UfhData.Set_sound(true);
                UfhData.SoundFlag = true;
                isCanceled = false;
                Log.e("startScan----->", "2");
                timer = new Timer();
                //
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //                        if (Scanflag)
                        //                            return;
                        Scanflag = true;
                        UfhData.read6c();
                        Log.e("startScan----->", "3");
                        mHandler.removeMessages(MSG_UPDATE_EMPLOYEE_NAME);
                        mHandler.sendEmptyMessage(MSG_UPDATE_EMPLOYEE_NAME);
                        Scanflag = false;
                    }
                }, 0, SCAN_INTERVAL);
                pxDetailScanPerson.setText("停止扫描");
            } else {
                cancelScan();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        /*if (!UfhData.isDeviceOpen()) {
            Toast.makeText(PeixiangdtActivity.this, "请先连接设备", Toast.LENGTH_LONG).show();
            return;
        }
        UfhData.Set_sound(true);
        UfhData.SoundFlag = false;
        Log.e("正在启动扫描", "1");
        if (timer == null) {
            Log.e("timer  ==  null","2");
            isCanceled = false;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    if (scanFlag) return;    //如果scanFlag == true ,表示正在扫描，返回
                    scanFlag = true;         //标记正在扫描
                    Log.e("zhouxin", "------onclick-------6c");
                    UfhData.read6c();
                    mHandler.removeMessages(MSG_UPDATE_EMPLOYEE_NAME);
                    mHandler.sendEmptyMessage(MSG_UPDATE_EMPLOYEE_NAME);
                    scanFlag = false;
                }
            }, 0, SCAN_INTERVAL);
            pxDetailScanPerson.setText("停止扫描");
        } else {
            cancelScan();
            Log.e("取消扫描", "");

        }
        Log.e("扫描完成", "");*/
    }

    /**
     * 取消扫描
     */
    private void cancelScan() {
        try {
            isCanceled = true;
            mHandler.removeMessages(MSG_UPDATE_EMPLOYEE_NAME);
            Log.e("cancelScan----->", "1");
            UfhData.Set_sound(false);
            UfhData.SoundFlag = false;
            if (timer != null) {
                Log.e("cancelScan----->", "2");
                timer.cancel();
                timer = null;
                pxDetailScanPerson.setText("添加人员");
                UfhData.scanResult6c.clear();
                UfhData.UhfGetData.CloseUhf();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*isCanceled = true;
        UfhData.Set_sound(false);
        Log.e("cancelScan","取消扫描");
        mHandler.removeMessages(MSG_UPDATE_EMPLOYEE_NAME);
        if (timer != null) {
            timer.cancel();
            timer = null;
            pxDetailScanPerson.setText("添加人员");
            UfhData.scanResult6c.clear();
            UhfGetData.CloseUhf();
        }*/
    }

    /**
     * 返回配箱activity
     */
    //TODO 如果存在扫描信息没有提交，提醒用户是否放弃提交
    private void backListPage() {
        if (pxDetailScanPerson.getText().toString().equals("停止扫描")) {
            Toast.makeText(PeixiangdtActivity.this, "请先停止扫描", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(getApplicationContext(), PeixiangActivity.class));
            finish();
        }
    }


    /**
     * 处理返回的结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ScanActivity.RESULT_CODE_SCAN) {
            switch (requestCode) {
                case ScanActivity.FLAG_PX_DETAIL:
                    Bundle bundle = data.getBundleExtra("bundle");
                    ArrayList<String> list = bundle.getStringArrayList("list");
                    for (String s : list) {
                        if (!mArrayList.contains(s) ){
                            if(mArrayList.size() < 19){
                                mArrayList.add(s);
                                arrayAdapter.notifyDataSetChanged();
                            }else if(mArrayList.size() == 19){
                                mArrayList.add(s);
                                arrayAdapter.notifyDataSetChanged();
                                new AlertDialog.Builder(mContext)
                                        .setMessage("提示")
                                        .setMessage("列表已满，是否确认提交？")
                                        .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //TODO 提交数据
                                                commitData();
                                            }
                                        })
                                        .setNegativeButton("取消", null)
                                        .show();
                            }
                        } else {
                            Toast.makeText(mContext, "存在重复数据", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case ScanActivity.FLAG_PX_PXNAME:
                    pxName = data.getStringExtra("pxName");
                    pxDetailBoxName.setText(pxName);
                    break;
                default:
                    break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 查询人员信息
     */
    public class MyThread extends Thread {
        @Override
        public void run() {
            try {
                //获取返回信息
                String responseContent = HttpUtils.
                        doPost(Constants.URL_FIT_BACK_EMPLOYEE, requestContent);
                Log.e("获取到的返回信息是 ------>", responseContent + "");
                EmployeeName employeeName = gson.fromJson(responseContent, EmployeeName.class);
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("code", employeeName.getCode());
                bundle.putString("message", employeeName.getMessage());
                bundle.putString("name", employeeName.getEmployeeName());
                bundle.putString("rfid", employeeName.getRfid());
                message.what = MSG_GET_NAME;
                message.setData(bundle);
                mHandler.sendMessage(message);
            } catch (NetworkErrorException e) {
                e.printStackTrace();
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //            if (isCanceled) return;
            switch (msg.what) {
                //处理关于扫描的信息
                //TODO 停止扫描，到后台匹配人员名字
                case MSG_UPDATE_EMPLOYEE_NAME:
                    data = UfhData.scanResult6c;
                    Iterator strings = data.keySet().iterator();
                    while (strings.hasNext()) {
                        String str = (String) strings.next();
                        Log.e("扫到的工牌信息", str);
                        if (!TextUtils.isEmpty(str)) {
                            if (pxDetailScanPerson.getText().toString().equals("停止扫描"))
                                cancelScan();
                            requestContent = "rfidCode=" + str;
                            //                            requestContent = "rfidCode=666433221";
                            MyThread myThread = new MyThread();
                            myThread.start();
                            break;
                        }

                    }
                    break;
                //查询employeeName返回结果
                //TODO 做一个实体类 有 name 和 rfidcode 属性
                case MSG_GET_NAME:
                    Bundle bundle = msg.getData();
                    EmployeeName employeeName = new EmployeeName();
                    employeeName.setCode(bundle.getString("code"));
                    employeeName.setMessage(bundle.getString("message"));
                    employeeName.setEmployeeName(bundle.getString("name"));
                    employeeName.setRfid(bundle.getString("rfid"));
                    if (employeeName.getCode().equals(ResultInfo.CODE_SUCCESS)) {
                        if (bankEmployee1 != null) {
                            if (bankEmployee1.getRfid().equals(employeeName.getRfid())) {
                                Toast.makeText(mContext, "人员重复，请重新扫描！", Toast.LENGTH_SHORT).show();
                                break;
                            } else {
                                bankEmployee2 = employeeName;
                                pxDetailPersonName2.setText(bankEmployee2.getEmployeeName());
                                //TODO 跳转到扫描配箱号
                                scanPeiXiang();
                                break;
                            }
                        }
                        bankEmployee1 = employeeName;
                        pxDetailPersonName1.setText(bankEmployee1.getEmployeeName());
                        break;
                    } else if (employeeName.getCode().equals(ResultInfo.CODE_ERROR)) {
                        Toast.makeText(PeixiangdtActivity.this, employeeName.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    break;
                //提交数据返回值
                case MSG_DATA_COMMIT:
                    Bundle bundle1 = msg.getData();
                    ResultInfo resultInfo1 = new ResultInfo();
                    resultInfo1.setCode(bundle1.getString("code"));
                    resultInfo1.setMessage(bundle1.getString("message"));
                    if (resultInfo1.getCode().equals(ResultInfo.CODE_SUCCESS)) {
                        if (waitDialogFragment.isVisible()) {
                            waitDialogFragment.setCancelable(true);
                            waitDialogFragment.dismiss();
                            waitDialogFragment.onDestroy();
                        }
                        Toast.makeText(PeixiangdtActivity.this, resultInfo1.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (resultInfo1.getCode().equals(ResultInfo.CODE_ERROR)) {
                        if (waitDialogFragment.isVisible()) {
                            waitDialogFragment.setCancelable(true);
                            waitDialogFragment.dismiss();
                            waitDialogFragment.onDestroy();
                        }
                        Toast.makeText(PeixiangdtActivity.this, "提交数据失败，请重新提交", Toast.LENGTH_LONG).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void scanPeiXiang() {
        Intent pxIntent = new Intent(PeixiangdtActivity.this, ScanActivity.class);
        pxIntent.setFlags(ScanActivity.FLAG_PX_PXNAME);
        startActivityForResult(pxIntent, ScanActivity.FLAG_PX_PXNAME);
        if (UfhData.isDeviceOpen())
            UhfGetData.CloseUhf(); //跳转页面前，结束连接
    }

    /**
     * 配箱页面提交数据
     * 使用url ：pei-xiangz!clearPlace.do；
     */
    private void commitData() {
        String arraylist = gson.toJson(mArrayList);
        Log.e("钱捆list列表----->", arraylist);
        if (bankEmployee1 == null || bankEmployee2 == null || pxName.isEmpty() || mArrayList.size() == 0) {
            Toast.makeText(PeixiangdtActivity.this, "数据不完整，无法提交！", Toast.LENGTH_LONG).show();
            return;
        }
        final String params = "employee1=" + bankEmployee1.getEmployeeName() + "&employee1_rfid=" + bankEmployee1.getRfid()
                + "&employee2=" + bankEmployee2.getEmployeeName() + "&employee2_rfid=" + bankEmployee2.getRfid()
                + "&pxName=" + pxName + "&list=" + arraylist;

        Log.i("params--->", params);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取返回信息
                    String responseContent = HttpUtils.
                            doPost(Constants.URL_DATA_COMMIT, params);
                    Log.i("获取到的返回信息是 ------>", responseContent);
                    ResultInfo resultInfo = gson.fromJson(responseContent, ResultInfo.class);
                    Message message = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("code", resultInfo.getCode());
                    bundle.putString("message", resultInfo.getMessage());
                    message.what = MSG_DATA_COMMIT;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                } catch (NetworkErrorException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //提示用户正在登陆，不允许其进行操作
        waitDialogFragment = new WaitDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("noteMsg", "正在提交数据请稍后");
        waitDialogFragment.setArguments(bundle);
        waitDialogFragment.setCancelable(false);
        waitDialogFragment.show(getFragmentManager(), "dialogFragment");
    }
}