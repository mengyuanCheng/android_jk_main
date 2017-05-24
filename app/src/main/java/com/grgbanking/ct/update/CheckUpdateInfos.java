package com.grgbanking.ct.update;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParser;


import android.content.Context;
import android.util.Xml;

public class CheckUpdateInfos {
	public static String getUpdataInfoXML(Context context,InputStream is) throws Exception{
		String down_url = null;
		XmlPullParser  parser = Xml.newPullParser();  
		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		while(type != XmlPullParser.END_DOCUMENT ){
			switch (type) {
			case XmlPullParser.START_TAG:
				if("version".equals(parser.getName())){
					String ver = parser.nextText();
					String current_version = context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;	
					if(ver == null || ver.equals(current_version)){
						return down_url;
					}
				}else if ("app_url".equals(parser.getName())){
					down_url =  parser.nextText();
				}
				break;
			}
			type = parser.next();
		}
		return down_url;
	}
	public static String getUpdataInfoJSON(Context context) throws Exception{
		String down_url = null;
		JSONTokener jsonParser = new JSONTokener(http("http://183.63.190.43:6600/hbct/checkupdate.json", null));
		try {
			JSONObject ipObject = (JSONObject) jsonParser.nextValue();
			String current_version = context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;	
			String server_version = ipObject.getString("version");
			if(server_version == null || server_version.equals(current_version)){
				return down_url;
			}
			return ipObject.getString("app_add");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return down_url;
	}
	
	public static String http(String url, Map<String, String> params) {
		StringBuffer buffer = new StringBuffer();
		URL u = null;
		HttpURLConnection con = null;
		StringBuffer sb = new StringBuffer();
		if (params != null) {
			for (Entry<String, String> e : params.entrySet()) {
				sb.append(e.getKey());
				sb.append("=");
				sb.append(e.getValue());
				sb.append("&");
			}
			sb.substring(0, sb.length() - 1);
		}
		try {
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
			osw.write(sb.toString());
			osw.flush();
			osw.close();
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
				String temp;
				while ((temp = br.readLine()) != null) {
					buffer.append(temp);
					buffer.append("\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return buffer.toString();
	}

}
