package com.grgbanking.ct.http;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.List;


public class HttpPostUtils extends AsyncTask<Object, Integer, ResultInfo> {
	 private String url;
	 private UICallBackDao uicallback;
	 private List<NameValuePair> params;

	 public HttpPostUtils(String url,List<NameValuePair> params,  UICallBackDao uicallback){
		 this.params=params;
		 this.uicallback=uicallback;
		 this.url=url;
	 }

	private ResultInfo post(){
		ResultInfo resultInfo=new ResultInfo();
		HttpPost httpPost = new HttpPost(url);
		try {
			Log.v("test","");
			httpPost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpClient client=new DefaultHttpClient();
			// 请求超时
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            // 读取超时
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000 );
			HttpResponse httpResponse=client.execute(httpPost);
        //TODO 这里只判断成功的情况，不太合理。
			if(httpResponse.getStatusLine().getStatusCode()==200){
//				String string =  EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8);
//				JSONObject jsonObject = JSONObject.fromObject(string);
//				resultInfo = (ResultInfo) JSONObject.toBean(jsonObject,ResultInfo.class);
//				resultInfo.setCode(jsonObject.getString("code"));
//				resultInfo.setJsonArray(jsonObject.getJSONArray("jsonArray"));
//				resultInfo.setJsonObject(jsonObject.getJSONObject("jsonObject"));
//				resultInfo.setMessage(jsonObject.getString("message"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultInfo.setCode("2");
			resultInfo.setMessage(e.getMessage());
		}
		return resultInfo;
	}

	@Override
	protected ResultInfo doInBackground(Object... params) {
		return post();
	}
    @Override
	protected void onPostExecute(ResultInfo result) {
        if (null != uicallback) {
            uicallback.callBack(result);
        }
    }

}
