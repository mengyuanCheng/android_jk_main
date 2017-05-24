package com.grgbanking.ct.utils;

import android.accounts.NetworkErrorException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 采用HttpUrlConnection的方式封装网络请求工具
 * Created by lzy on 2017/5/9.
 */
public class HttpUtils {
    /**
     * 采用 <i>post</i> 的方式向服务器发送请求.
     *
     * @param url     请求的网络地址
     * @param content 请求的参数
     * @return 从服务器接收到的返回信息
     */
    public static String doPost(String url, String content) throws NetworkErrorException {
        HttpURLConnection connection = null;
        try {
            URL mURL = new URL(url);
            connection = (HttpURLConnection) mURL.openConnection();

            connection.setRequestMethod("POST");// 设置请求方法为post
            connection.setReadTimeout(5000);// 设置读取超时为5秒
            connection.setConnectTimeout(10000);// 设置连接网络超时为10秒
            connection.setDoOutput(true);// 设置此方法,允许向服务器输出内容

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(content.getBytes());
            outputStream.flush();
            outputStream.close();
            int responseCode = connection.getResponseCode();// 调用此方法就不必再使用conn.connect()方法
            if (responseCode == 200) {

                InputStream is = connection.getInputStream();
                String response = getStringFromInputStream(is);
                return response;
            } else {
                throw new NetworkErrorException("response status is " + responseCode);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    /**
     * 采用<i>get</i>方式向服务器发送请求
     * @param url
     * @return
     */
    public static String doGet(String url) {
        HttpURLConnection conn = null;
        try {
            // 利用string url构建URL对象
            URL mURL = new URL(url);
            conn = (HttpURLConnection) mURL.openConnection();

            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(10000);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {

                InputStream is = conn.getInputStream();
                String response = getStringFromInputStream(is);
                return response;
            } else {
                throw new NetworkErrorException("response status is " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (conn != null) {
                conn.disconnect();
            }
        }

        return null;
    }


    private static String getStringFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 模板代码 必须熟练
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();// 把流中的数据转换成字符串,采用的编码是utf-8(模拟器默认编码)
        os.close();
        return state;
    }


}
