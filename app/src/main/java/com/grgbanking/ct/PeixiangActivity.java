package com.grgbanking.ct;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.grgbanking.ct.rfid.UfhData;
import com.grgbanking.ct.rfid.UfhData.UhfGetData;
import com.grgbanking.ct.utils.ScanActivity;

/**
 * Created by Administrator on 2016/7/13.
 */


public class PeixiangActivity extends Activity implements View.OnClickListener {


    private Button pxBack;   //返回button
    private Button pxButton; //配箱button

    private long firstTime = 0;   //第一次点击返回键的时间
    Context mContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peixiang);

        pxBack = (Button) findViewById(R.id.net_sysout_view);
        pxButton = (Button) findViewById(R.id.peixiang_button);
        mContext = PeixiangActivity.this;

        // 返回button 点击事件
        pxBack.setOnClickListener(this);

        // 配箱button 点击事件
        pxButton.setOnClickListener(this);
        // 二维码扫描 button 点击事件
    }

    /**
     * 根据不同 button 的 id 做相应的操作
     *
     * @param v   switch(v.id)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回 button
            case R.id.net_sysout_view:
                if(UfhData.isDeviceOpen()){
                    UhfGetData.CloseUhf();
                }
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                break;
            // 配箱 button
            case R.id.peixiang_button:
                Intent intent = new Intent();
                intent.setClass(PeixiangActivity.this, PeixiangdtActivity.class);
                startActivity(intent);
                break;
            //二维码扫描 button

            default:
                break;
        }

    }

    /**
     * 设置双击返回键退出程序，两次点击的时间间隔为2秒
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - firstTime > 2000) {
                Toast.makeText(PeixiangActivity.this, "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            } else {
                if (UfhData.isDeviceOpen()){
                    UhfGetData.CloseUhf();
                }
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



}
