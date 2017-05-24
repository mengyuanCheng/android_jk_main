package com.grgbanking.ct.http;

import android.os.AsyncTask;
import android.util.Log;

import com.grgbanking.ct.entity.LoginUser;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 用于登陆使用的异步线程
 * LoginUser ---> 需要登陆的用户，带有用户名密码的用户；
 * Object ---> 不要进行更新进度的操作；
 * Result ---> 从服务器上获取到的数据储存在Result中；
 * Created by lzy on 2017/5/4.
 */

public class LoginAsyncTask extends AsyncTask<LoginUser,Object,ResultInfo>{
    /**
     * 如果表示url,表示要访问的地址
     * 也可以将其表示为本地数据库要查询的东西。
     */
    private String url;
    private LoginUser loginUser; //储存登陆名，密码的 user；
    private UICallBackDao uiCallBack;  //接口，用来实现更新UI层的操作；

    public LoginAsyncTask(String url , LoginUser user,UICallBackDao uiCallBackDao) {
        this.url = url;
        this.loginUser = user;
        this.uiCallBack = uiCallBackDao;
    }

    /**
     * 在后台线程中进行的操作
     * @param  params
     * @return
     */
    @Override
    protected ResultInfo doInBackground(LoginUser... params) {
        ResultInfo resultInfo = post();
        return resultInfo;
    }

    /**
     * doInBackground（）方法执行完以后，对返回值进行处理。可以进行一些UI操作
     * @param resultInfo  访问网络后返回的信息
     */
    @Override
    protected void onPostExecute(ResultInfo resultInfo) {
        if (null != uiCallBack) {
            uiCallBack.callBack(resultInfo);
        }
    }

    /**
     *  根据 url、loginUser 访问服务器，并将服务器返回的结果保存在ResultInfo中
     * @return ResultInfo 储存返回信息。
     */
    private ResultInfo post() {
        ResultInfo resultInfo = new ResultInfo();

        // HttpPostUtils 构造器的列表参数。
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("login_name", loginUser.getLoginName()));
        list.add(new BasicNameValuePair("login_password", loginUser.getPassword()));

        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(list,HTTP.UTF_8));//出现UnsupportedEncoding异常。
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);

            HttpResponse response = client.execute(httpPost);//出现io异常

            //返回码等于==200 时，
            if(response.getStatusLine().getStatusCode() == 200){
                String string =  EntityUtils.toString(response.getEntity(),HTTP.UTF_8);//出现IO异常
                JSONObject jsonObject = new JSONObject(string); //出现 JSON 异常
//			JSONObject jsonObject = JSONObject.fromObject(string);
                resultInfo.setCode(jsonObject.getString("code"));
                resultInfo.setMessage(jsonObject.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setCode("2");
            resultInfo.setMessage(e.getMessage());
        }

        return resultInfo;
    }
}
