package com.grgbanking.ct;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.grgbanking.ct.cach.DataCach;
import com.grgbanking.ct.entity.LoginUser;
import com.grgbanking.ct.entity.PdaLoginMessage;
import com.grgbanking.ct.http.HttpPostUtils;
import com.grgbanking.ct.http.ResultInfo;
import com.grgbanking.ct.http.UICallBackDao;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

public class NetOutInActivity extends Activity {

	private Context context;
	Button sysOutButton;
	Button netInButton = null;
	Button netOutButton = null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = this.getApplicationContext();
		super.onCreate(savedInstanceState);
//		startService(new Intent(context, GrgbankService.class));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.netoutin);

        sysOutButton=(Button) findViewById(R.id.net_sysout_view);
        sysOutButton.setOnClickListener(saomiaoButtonclick);

        netInButton = (Button) findViewById(R.id.peixiang_button);
        netOutButton = (Button) findViewById(R.id.net_out_button);

        //初始化缓存，将缓存清空
        DataCach.clearAllDataCach();

        netInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            	showWaitDialog("正在加载网点入库信息...");
            	LoginUser loginUser = DataCach.loginUser;

            	List<NameValuePair> params=new ArrayList<NameValuePair>();
            	params.add(new BasicNameValuePair("login_name", loginUser.getLoginName()));
            	params.add(new BasicNameValuePair("scanning_type", Constants.LOGIN_NET_IN));

            	//访问后台服务器进行登录操作
            	new HttpPostUtils(Constants.URL_NET_OUTIN, params, new UICallBackDao() {
					@Override
					public void callBack(ResultInfo resultInfo) {
		            	if(!ResultInfo.CODE_SUCCESS.equals(resultInfo.getCode())){
		            		Toast.makeText(context, resultInfo.getMessage(), Toast.LENGTH_LONG).show();
		            		hideWaitDialog();
		            		return;
		            	}

//	            		JSONObject jsonObject = resultInfo.getJsonObject();
//	            		PdaLoginMessage pdaLoginMessage = PdaLoginMessage.JSONtoPdaLoginMessage(jsonObject);
//	            		DataCach.setPdaLoginMessage(pdaLoginMessage);
//
//	            		DataCach.netType = Constants.NET_COMMIT_TYPE_IN;

	            		hideWaitDialog();

		                Intent intent = new Intent();
		                intent.setClass(NetOutInActivity.this, MainActivity.class);
		                startActivity(intent);
		                finish();
					}
				}).execute();
            }
        });

        netOutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            	showWaitDialog("正在加载网点出库信息...");
            	LoginUser loginUser = DataCach.loginUser;

            	List<NameValuePair> params=new ArrayList<NameValuePair>();
            	params.add(new BasicNameValuePair("login_name", loginUser.getLoginName()));
				System.out.print("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+loginUser.getLoginName());
				params.add(new BasicNameValuePair("scanning_type", Constants.LOGIN_NET_OUT));

            	//访问后台服务器进行登录操作
            	new HttpPostUtils(Constants.URL_NET_OUTIN, params, new UICallBackDao() {
					@Override
					public void callBack(ResultInfo resultInfo) {
		            	if(!ResultInfo.CODE_SUCCESS.equals(resultInfo.getCode())){
		            		Toast.makeText(context, resultInfo.getMessage(), Toast.LENGTH_LONG).show();
		            		hideWaitDialog();
		            		return;
		            	}

//	            		JSONObject jsonObject = resultInfo.getJsonObject();
//	            		PdaLoginMessage pdaLoginMessage = PdaLoginMessage.JSONtoPdaLoginMessage(jsonObject);
//	            		DataCach.setPdaLoginMessage(pdaLoginMessage);

	            		DataCach.netType = Constants.NET_COMMIT_TYPE_OUT;

	            		hideWaitDialog();

		                Intent intent = new Intent();
		                intent.setClass(NetOutInActivity.this, MainActivity.class);
		                startActivity(intent);
		                finish();
					}
				}).execute();
            }
        });
	}

	private ProgressDialog pd=null;

	private void showWaitDialog(String msg){
		if(pd == null){
			pd = new ProgressDialog(this);
		}
		pd.setCancelable(false);
		pd.setMessage(msg);
		pd.show();
	}

	private void hideWaitDialog(){
		if(pd!=null){
			pd.cancel();
		}
	}

	OnClickListener saomiaoButtonclick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			//清空缓存
			DataCach.clearAllDataCach();

			Intent intent = new Intent();
            intent.setClass(NetOutInActivity.this, LoginActivity.class);
            startActivity(intent);
			finish();
		}
	};
}
