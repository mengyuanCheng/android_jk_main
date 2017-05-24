package com.grgbanking.ct.location;

import java.util.Date;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.grgbanking.ct.utils.LogUtils;

public class LocationManager {
	
	/**
	 * 实时获取经纬度
	 * @param context
	 */
	public static void location(final Context context){
		
		LocationClient locationClient = new LocationClient(context);
        // 设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 是否打开GPS
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setProdName("LocationDemo"); // 设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(0);// 设置定时定位的时间间隔。单位毫秒
        option.setAddrType("all");
        locationClient.setLocOption(option);
 
        // 注册位置监听器
        locationClient.registerLocationListener(new BDLocationListener() {
 
            @Override
            public void onReceiveLocation(BDLocation location) {
            	if (location == null)
        			return;
        		StringBuffer sb = new StringBuffer(256);
        		sb.append("time : ");
        		sb.append(location.getTime());
        		sb.append("\nerror code : ");
        		sb.append(location.getLocType());
        		sb.append("\nlatitude : ");
        		sb.append(location.getLatitude());
        		sb.append("\nlontitude : ");
        		sb.append(location.getLongitude());
        		sb.append("\nradius : ");
        		sb.append(location.getRadius());
        		if (location.getLocType() == BDLocation.TypeGpsLocation) {
        			sb.append("\nspeed : ");
        			sb.append(location.getSpeed());
        			sb.append("\nsatellite : ");
        			sb.append(location.getSatelliteNumber());
        		} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
        			sb.append("\naddr : ");
        			sb.append(location.getAddrStr());
        		}
        		LogUtils.toastLog(context,sb.toString());
            }
        });
	}
}
