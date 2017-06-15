package com.grgbanking.ct;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.grgbanking.ct.entity.EmployeeName;
import com.grgbanking.ct.http.ResultInfo;
import com.grgbanking.ct.rfid.UfhData;
import com.grgbanking.ct.utils.HttpUtils;
import com.grgbanking.ct.utils.ScanActivity;
import com.grgbanking.ct.utils.WaitDialogFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class HandoverWorkActivity extends Activity implements View.OnClickListener {

    private Context mContext;
    private Button btnBack;  //返回按钮
    private Button btnScanPerson;  //扫描人员信息
    private Button btnScanPeiXiang;   //扫描配箱信息
    private Button btnCommit;     //提交数据
    private Spinner spinner;      //下拉菜单
    private TextView tvPersonName1;   //人员名字1
    private TextView tvPersonName2;   //人员名字2
    private TextView tvPersonName12;
    private TextView tvPersonName22;
    private WaitDialogFragment waitDialogFragment;  //提交数据等待页面
    private ListView pxListView;      //存放配箱信息的listView；
    private ArrayAdapter arrayAdapter;
    ArrayList<String> mArrayList = new ArrayList<>();    //存放peixiang信息的list

    private Timer timer;
    private boolean isCanceled = true;
    private boolean Scanflag = false;                //false 表示没有在扫描
    private static final int MSG_UPDATE_EMPLOYEE_NAME = 0;
    private static final int SCAN_INTERVAL = 10;  //扫描间隔
    private Map<String, Integer> data;

    private static final int MSG_HW_GET_NAME = 10;       //查询名字what 标记
    private static final int MSG_HW_DATA_COMMIT = 20;   //提交数据what 标记
    private String requestContent;

    private Gson gson = new Gson();
    private EmployeeName bankEmployee1;                //用来显示工牌扫描出的结果在后台匹配的人员信息
    private EmployeeName bankEmployee12;
    private EmployeeName bankEmployee2;
    private EmployeeName bankEmployee22;
    private String moneyAmount; //面额  需上传


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_handover_work);
        mContext = HandoverWorkActivity.this;
        findViewById();
        arrayAdapter = new ArrayAdapter(this, R.layout.simple_listview_item,
                R.id.listView_item_textView, mArrayList);
        pxListView.setAdapter(arrayAdapter);

    }

    private void findViewById() {
        btnBack = (Button) findViewById(R.id.hw_back);
        btnBack.setOnClickListener(this);
        btnScanPerson = (Button) findViewById(R.id.hw_person_scan);
        btnScanPerson.setOnClickListener(this);
        btnScanPeiXiang = (Button) findViewById(R.id.hw_money_scan);
        btnScanPeiXiang.setOnClickListener(this);
        btnCommit = (Button) findViewById(R.id.hw_commit);
        btnCommit.setOnClickListener(this);
        spinner = (Spinner) findViewById(R.id.hw_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                moneyAmount = parent.getItemAtPosition(position).toString();
                Log.i("选择的面额是----->", moneyAmount);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tvPersonName1 = (TextView) findViewById(R.id.hw_show_name1);  //显示人行人员1
        tvPersonName12 = (TextView) findViewById(R.id.hw_show_name12);//显示人行人员2
        tvPersonName2 = (TextView) findViewById(R.id.hw_show_name2);  //显示银行人员1
        tvPersonName22 = (TextView) findViewById(R.id.hw_show_name22);//显示银行人员2
        pxListView = (ListView) findViewById(R.id.hw_listView);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*返回操作*/
            case R.id.hw_back:
                if (btnBack.getText().toString().equals("停止扫描")) {
                    Toast.makeText(mContext, "请先停止扫描", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
                break;
            /*扫描人员信息*/
            case R.id.hw_person_scan:
                if (!UfhData.isDeviceOpen()) {
                    connectDevice();
                    if (!UfhData.isDeviceOpen())
                        break;
                }
                if (btnScanPerson.getText().toString().equals("添加人员")) {
                    startScan();
                } else if (btnScanPerson.getText().toString().equals("停止扫描")) {
                    cancelScan();
                }
                break;
            /*扫描peixing信息*/
            case R.id.hw_money_scan:
                Intent intent = new Intent(mContext, ScanActivity.class);
                intent.setFlags(ScanActivity.FLAG_PX_DETAIL);
                startActivityForResult(intent, ScanActivity.FLAG_PX_DETAIL);
                if (UfhData.isDeviceOpen())
                    UfhData.UhfGetData.CloseUhf(); //跳转页面前结束连接
                break;
            /* 提交数据*/
            case R.id.hw_commit:

                String arraylist = gson.toJson(mArrayList);
                Log.e("钱捆list列表----->", arraylist);
                if (bankEmployee1 == null || bankEmployee2 == null || bankEmployee12 == null || bankEmployee22 == null || mArrayList.size() == 0) {
                    Toast.makeText(mContext, "数据不完整，无法提交！", Toast.LENGTH_LONG).show();
                    return;
                }
                final String params = "employee1=" + bankEmployee1.getEmployeeName() + "&employee1_rfid=" + bankEmployee1.getRfid()
                        + "&employee12=" + bankEmployee12.getEmployeeName() + "&employee12_rfid" + bankEmployee12.getRfid()
                        + "&employee2=" + bankEmployee2.getEmployeeName() + "&employee2_rfid=" + bankEmployee2.getRfid()
                        + "&employee22=" + bankEmployee22.getEmployeeName() + "&employee2_rfid=" + bankEmployee22.getRfid()
                        + "&list=" + arraylist;

                Log.i("params--->", params);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //获取返回信息
                            String responseContent = HttpUtils.
                                    doPost(Constants.URL_Upload_Associate, params);
                            Log.e("获取到的返回信息是 ------>", responseContent + "");
                            ResultInfo resultInfo = gson.fromJson(responseContent, ResultInfo.class);
                            Message message = Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putString("code", resultInfo.getCode());
                            bundle.putString("message", resultInfo.getMessage());
                            message.what = MSG_HW_DATA_COMMIT;
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                        } catch (NetworkErrorException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

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
        int result = UfhData.UhfGetData.OpenUhf(tty_speed, addr, 4, 1, null);
        if (result == 0) {
            UfhData.UhfGetData.GetUhfInfo();
        } else {
            Toast.makeText(mContext, "连接设备失败，请关闭程序重新登录", Toast.LENGTH_SHORT).show();
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
                timer = new Timer();
                Log.e("startScan----->", "2");
                //
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //                        if (Scanflag)
                        //                            return;
                        Scanflag = true;
                        Log.e("startScan----->", "3");
                        UfhData.read6c();
                        Log.e("startScan----->", "4");
                        mHandler.removeMessages(MSG_UPDATE_EMPLOYEE_NAME);
                        mHandler.sendEmptyMessage(MSG_UPDATE_EMPLOYEE_NAME);
                        Scanflag = false;
                    }
                }, 0, SCAN_INTERVAL);
                btnScanPerson.setText("停止扫描");
            } else {
                cancelScan();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 取消扫描
     */
    private void cancelScan() {
        try {
            Log.e("cancelScan----->", "1");
            isCanceled = true;
            UfhData.Set_sound(false);
            UfhData.SoundFlag = false;
            mHandler.removeMessages(MSG_UPDATE_EMPLOYEE_NAME);
            if (timer != null) {
                Log.e("cancelScan----->", "2");
                timer.cancel();
                timer = null;
                btnScanPerson.setText("添加人员");
                UfhData.scanResult6c.clear();
                UfhData.UhfGetData.CloseUhf();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyThread extends Thread {
        @Override
        public void run() {
            try {
                //获取返回信息
                String responseContent = HttpUtils.
                        doPost(Constants.URL_FIND_BANK_EMPLOYEE, requestContent);
                Log.e("获取到的返回信息是 ------>", responseContent);
                EmployeeName employeeName = gson.fromJson(responseContent, EmployeeName.class);
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("code", employeeName.getCode());
                bundle.putString("message", employeeName.getMessage());
                bundle.putString("name", employeeName.getEmployeeName());
                bundle.putString("rfid", employeeName.getRfid());
                message.what = MSG_HW_GET_NAME;
                message.setData(bundle);
                mHandler.sendMessage(message);
            } catch (NetworkErrorException e) {
                e.printStackTrace();
            }
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
                    assert list != null;
                    for (String s : list) {
                        if (!mArrayList.contains(s) && mArrayList.size() <= 20) {
                            mArrayList.add(s);
                        }
                    }
                    arrayAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_EMPLOYEE_NAME:
                    //                    if (isCanceled)
                    //                        return;
                    data = UfhData.scanResult6c;
                    Log.e("getData----->", data.toString());
                    Iterator it = data.keySet().iterator();
                    while (it.hasNext()) {
                        String str = (String) it.next();
                        Log.e("扫到的工牌信息", str);
                        if (!TextUtils.isEmpty(str)) {
                            if (btnScanPerson.getText().toString().equals("停止扫描"))
                                cancelScan();
                            requestContent = "rfidCode=" + str;
                            MyThread myThread = new MyThread();
                            myThread.start();
                            return;
                        }

                    }
                    break;
                /*处理网络请求 得到人员信息*/
                //TODO 判断人员是否是人民银行的人
                case MSG_HW_GET_NAME:
                    Bundle bundle = msg.getData();
                    EmployeeName employeeName = new EmployeeName();
                    employeeName.setCode(bundle.getString("code"));
                    employeeName.setMessage(bundle.getString("message"));
                    employeeName.setEmployeeName(bundle.getString("name"));
                    employeeName.setRfid(bundle.getString("rfid"));
                    if (employeeName.getCode().equals(ResultInfo.CODE_ERROR)) {

                        Toast.makeText(mContext, employeeName.getMessage(), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (employeeName.getCode().equals(EmployeeName.CODE_REN)) {
                        //如果人行的 人员1 是空的时
                        if (bankEmployee1 == null) {

                            bankEmployee1 = employeeName;
                            tvPersonName1.setText(bankEmployee1.getEmployeeName());
                            break;
                            // 如果人员1 不为空 ,但人员 2 为空
                        } else if (bankEmployee1 != null && bankEmployee12 == null) {
                            if (bankEmployee1.getRfid().equals(employeeName.getRfid())) {

                                showAlertDialog("人行人员信息重复,请重新扫描");
                                break;
                            } else {

                                bankEmployee12 = employeeName;
                                tvPersonName12.setText(bankEmployee12.getEmployeeName());
                                break;
                            }
                        }
                    } else if (employeeName.getCode().equals(EmployeeName.CODE_SUCCESS)) {
                        //如果银行的 人员1 是空的时
                        if (bankEmployee2 == null) {

                            bankEmployee2 = employeeName;
                            tvPersonName2.setText(bankEmployee2.getEmployeeName());
                            break;
                            // 如果人员1 不为空 ,但人员 2 为空
                        } else if (bankEmployee2 != null && bankEmployee22 == null) {
                            if (bankEmployee2.getRfid().equals(employeeName.getRfid())) {

                                showAlertDialog("银行人员信息重复,请重新扫描");
                                break;
                            } else {

                                bankEmployee22 = employeeName;
                                tvPersonName22.setText(bankEmployee22.getEmployeeName());
                                break;
                            }
                        }
                    }
                    Toast.makeText(mContext, "人员信息已添加!", Toast.LENGTH_SHORT).show();
                    break;
                /*提交数据到服务器*/
                case MSG_HW_DATA_COMMIT:
                    Bundle bundle1 = msg.getData();
                    ResultInfo resultInfo1 = new ResultInfo();
                    Log.e("上传数据resultinfo--->", resultInfo1.toString());
                    resultInfo1.setCode(bundle1.getString("code"));
                    resultInfo1.setMessage(bundle1.getString("message"));
                    if (resultInfo1.getCode().equals(ResultInfo.CODE_SUCCESS)) {

                        Toast.makeText(mContext, resultInfo1.getMessage(), Toast.LENGTH_SHORT).show();
                        clearView();
                    } else if (resultInfo1.getCode().equals(ResultInfo.CODE_ERROR)) {

                        Toast.makeText(mContext, "提交数据失败，请重新提交", Toast.LENGTH_LONG).show();
                        clearView();
                    }
                    break;
                default:
                    break;
            }

        }
    };

    void clearView() {
        tvPersonName1.setText("");
        tvPersonName2.setText("");
        bankEmployee1 = null;
        bankEmployee2 = null;
        bankEmployee12 = null;
        bankEmployee22 = null;
        mArrayList.clear();
        arrayAdapter.notifyDataSetChanged();
    }

    void showAlertDialog(String msg) {
        new AlertDialog.Builder(mContext)
                .setTitle("提示")
                .setMessage(msg)
                .setPositiveButton("确定", null)
                .show();
    }

}
