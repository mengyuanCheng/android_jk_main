package com.grgbanking.ct.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.device.scanner.configuration.Triggering;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ct.PeixiangdtActivity;
import com.grgbanking.ct.R;

import java.util.ArrayList;

public class ScanActivity extends Activity implements OnClickListener {
    /*
     * action: android.intent.ACTION_DECODE_DATA   
     * string tag: "barcode_string"   
     * length tag: "length"    
     * data tag: "barcode"
     * */
    private final static String SCAN_ACTION = "android.intent.ACTION_DECODE_DATA";//

    public static final int RESULT_CODE_SCAN = 00;
    public static final int FLAG_PX_DETAIL = 1;      //识别是pxDetailActivity 跳转来的；
    public static final int FLAG_PX = 2;       //识别是peixiangActivity 跳转来的；
    public static final int FLAG_PX_PXNAME = 3;
    private int mFlag;   //储存接收到的flag

    private TextView showScanResult;   //显示正在扫描的二维码信息
    private Button btnCommit;          //提交二维码信息到pxDetail页面
    private Button btnStartScan;       //开始扫描
    private Button btnStopScan;        //结束扫描

    private ListView mListView;        //用于扫描已经扫过的二维码信息
    private String pxName;
    private ArrayAdapter arrayAdapter;
    private ArrayList<String> mList = new ArrayList<>();

    private int type;
    private int outPut;

    private Vibrator mVibrator;       //震动器
    private ScanManager mScanManager;
    private SoundPool soundpool = null;
    private int soundid;
    private String barcodeStr;
    private boolean isScaning = false;   //用于指示是否正在扫描
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            isScaning = false;
            soundpool.play(soundid, 1, 1, 0, 0, 1);
            showScanResult.setText("");
            mVibrator.vibrate(100);
            byte[] barcode = intent.getByteArrayExtra("barcode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            android.util.Log.i("debug", "----codetype--" + temp);
            barcodeStr = new String(barcode, 0, barocodelen);
            //String barcodeStr = intent.getStringExtra("barcode_string");
            showScanResult.setText(barcodeStr);
            //如果mFlag == FLAG_PX_DETAIL，即是要扫描钱捆
            if(mFlag == FLAG_PX_DETAIL) {
                if (mList.contains(barcodeStr)) {
                    Toast.makeText(ScanActivity.this, "扫描结果已经存在", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    mList.add(barcodeStr);
                    if (arrayAdapter != null) {
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }else if(mFlag == FLAG_PX_PXNAME){   //表示扫描的是peixiang号
                pxName = barcodeStr;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sacn_activity);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setupView();

        Intent intent = getIntent();
        mFlag = intent.getFlags();   //获取flag

        //mList.add("在这里存放扫描结果");
        arrayAdapter = new ArrayAdapter(this, R.layout.simple_listview_item, R.id.listView_item_textView, mList);
        mListView.setAdapter(arrayAdapter);


    }

    private void initScan() {
        // TODO Auto-generated method stub
        mScanManager = new ScanManager();
        mScanManager.openScanner();
        mScanManager.switchOutputMode(0);
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load("/etc/Scan_new.ogg", 1);
    }

    private void setupView() {
        mListView = (ListView) findViewById(R.id.scan_listView);
        showScanResult = (TextView) findViewById(R.id.scan_result);
        btnCommit = (Button) findViewById(R.id.scan_data_commit);
        btnCommit.setOnClickListener(this);
        btnStartScan = (Button) findViewById(R.id.scan_start);
        btnStartScan.setOnClickListener(this);
        btnStopScan = (Button) findViewById(R.id.scan_stop);
        btnStopScan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //开始扫描
            case R.id.scan_start:
                mScanManager.stopDecode();
                isScaning = true;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mScanManager.startDecode();
                break;
            //停止扫描
            case R.id.scan_stop:
                mScanManager.stopDecode();
                break;
            /*提交数据*/
            case R.id.scan_data_commit:
                if (mScanManager.getTriggerMode() != Triggering.CONTINUOUS)
                    mScanManager.setTriggerMode(Triggering.CONTINUOUS);
                commitData();
                break;
            default:
                break;
        }
    }

    /**
     * 提交数据到peixiangdtActivity
     */
    private void commitData() {
        //如果是从pxDetail跳转到的此页面，则提交数据到pxDetail页面
        if (mFlag == FLAG_PX_DETAIL) {
            Intent commitIntent = new Intent(ScanActivity.this, PeixiangdtActivity.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("list", mList);
            commitIntent.putExtra("bundle", bundle);
            this.setResult(RESULT_CODE_SCAN,commitIntent);
            finish();
        }else if(mFlag == FLAG_PX_PXNAME){
            Intent pxIntent = new Intent();
            pxIntent.putExtra("pxName",pxName);
            this.setResult(RESULT_CODE_SCAN,pxIntent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (mScanManager != null) {
            mScanManager.stopDecode();
            isScaning = false;
        }
        unregisterReceiver(mScanReceiver);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initScan();
        showScanResult.setText("");
        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        registerReceiver(mScanReceiver, filter);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            return true;
        }
        return super.onKeyDown(keyCode,event);

    }


}
