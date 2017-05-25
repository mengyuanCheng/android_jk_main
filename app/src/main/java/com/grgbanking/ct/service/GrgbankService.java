package com.grgbanking.ct.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.grgbanking.ct.Constants;
import com.grgbanking.ct.database.Person;
import com.grgbanking.ct.database.PersonTableHelper;
import com.grgbanking.ct.http.HttpPostUtils;
import com.grgbanking.ct.http.ResultInfo;
import com.grgbanking.ct.http.UICallBackDao;
import com.grgbanking.ct.utils.StringTools;

public class GrgbankService extends Service {

	Person person;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		getLocation();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getApplicationContext().startService(
				new Intent(getApplicationContext(), GrgbankService.class));
	}

	/**
	 * 获取本机经纬度地址 一般需要室外环境测试
	 */
	void getLocation() {
		LocationClient locationClient = new LocationClient(
				getApplicationContext());
		// 设置定位条件
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 是否打开GPS
		option.setIsNeedAddress(true);
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setScanSpan(5*60 * 1000);// 设置定时定位的时间间隔。单位毫秒
		option.setAddrType("all");
		option.setCoorType("bd09ll");
		locationClient.setLocOption(option);
		 person = PersonTableHelper.queryEntity(this);
		// 注册位置监听器
		locationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (person==null||StringTools.isEmpty(person.getUser_id())) {
					return;
				}
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("latitude", String
						.valueOf(location.getLatitude())));
				params.add(new BasicNameValuePair("longitude", String
						.valueOf(location.getLongitude())));
				params.add(new BasicNameValuePair("address", location
						.getAddrStr()));
				params.add(new BasicNameValuePair("userId", person.getUser_id()));
 				params.add(new BasicNameValuePair("userName", person.getUser_name()));
				new HttpPostUtils(Constants.URL_SAVE_GPS, params,
						new UICallBackDao() {

							@Override
							public void callBack(ResultInfo resultInfo) {
							}
						}).execute();
			}
		});
		locationClient.start();
	}

}
