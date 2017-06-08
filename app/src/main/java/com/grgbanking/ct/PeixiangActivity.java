package com.grgbanking.ct;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.grgbanking.ct.rfid.UfhData;
import com.grgbanking.ct.rfid.UfhData.UhfGetData;

/**
 * Created by Administrator on 2016/7/13.
 */


public class PeixiangActivity extends Activity implements View.OnClickListener {


    private Button pxBack;   //返回button
    private Button pxButton; //配箱button
    private Button pxHandover;  //交接页面
    private long firstTime = 0;   //第一次点击返回键的时间
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peixiang);

        mContext = PeixiangActivity.this;
        pxBack = (Button) findViewById(R.id.net_sysout_view);
        pxButton = (Button) findViewById(R.id.peixiang_button);
        pxHandover = (Button) findViewById(R.id.peixiang_handover_button);

        // 返回button 点击事件
        pxBack.setOnClickListener(this);

        // 配箱button 点击事件
        pxButton.setOnClickListener(this);
        // 配箱交接页面 点击跳转
        pxHandover.setOnClickListener(this);
    }

    /**
     * 根据不同 button 的 id 做相应的操作
     *
     * @param v switch(v.id)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回 button
            case R.id.net_sysout_view:
                if (UfhData.isDeviceOpen()) {
                    UhfGetData.CloseUhf();
                }
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                break;
            // 配箱 button
            case R.id.peixiang_button:
                Intent intent = new Intent();
                intent.setClass(mContext, PeixiangdtActivity.class);
                startActivity(intent);
                break;
            //配箱交接 button
            case R.id.peixiang_handover_button:
                startActivity(new Intent(mContext, ContainerHandoverActivity.class));
                break;
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
                if (UfhData.isDeviceOpen()) {
                    UhfGetData.CloseUhf();
                }
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
