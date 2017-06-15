package com.grgbanking.ct;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.grgbanking.ct.cach.DataCach;
import com.grgbanking.ct.database.Person;
import com.grgbanking.ct.http.ResultInfo;
import com.grgbanking.ct.utils.LoginUtil;
import com.grgbanking.ct.utils.StringTools;
import com.grgbanking.ct.utils.WaitDialogFragment;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends Activity implements OnClickListener {

    private static final int ERROR_REQUEST = 000;  //请求错误码
    private static final int SUSSESS_REQUEST = 111; // 请求成功码
    private Button loginButtonView;
    private EditText loginNameView;
    private EditText passwordView;
    private CheckBox remPasswordView;

    String loginNameViewValue = null; //UI控件内容
    String passwordViewValue = null;//UI控件内容
    String userId = null; //登录成功后的用户ID
    String userName = null;//登录成功后的用户姓名
    private Context context;
    private Person person;
    TextView detail_branch_name;

    private WaitDialogFragment waitDialogFragment;  //登陆提示界面
    private static String noteMsg = "正在登陆...";   //提示信息

    private LoginUtil loginUtil;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);

        //控件初始化
        findViewById();


        //判断是否记住密码，如果有记住用户名密码，将自动将用户名密码控件内容自动填充
        loginUtil = new LoginUtil(context);
        if (loginUtil.getBooleanInfo(LoginUtil.SAVE_PASSWORD)) {
            remPasswordView.setChecked(true);
            loginNameView.setText(loginUtil.getStringInfo(LoginUtil.USER_NAME));
            passwordView.setText(loginUtil.getStringInfo(LoginUtil.USER_PASSWORD));
        }
        loginButtonView.setOnClickListener(this);
    }


    /**
     * findViewById.
     */
    public void findViewById() {
        remPasswordView = (CheckBox) this.findViewById(R.id.cb);
        loginNameView = (EditText) this.findViewById(R.id.username_edit);
        passwordView = (EditText) this.findViewById(R.id.password_edit);
        loginButtonView = (Button) this.findViewById(R.id.login_button);
        detail_branch_name = (TextView) this.findViewById(R.id.detail_branch_name);
    }

    /**
     * 登陆button点击事件
     *
     * @param v loginButton
     */
    @Override
    public void onClick(View v) {
        loginButtonView.setClickable(false);
        loginNameViewValue = loginNameView.getText().toString();
        passwordViewValue = passwordView.getText().toString();
        if (StringTools.isEmpty(loginNameViewValue) || StringTools.isEmpty(passwordViewValue)) {
            Log.v("V1", "用户名或密码为空");
            Toast.makeText(context, "用户名或密码不能为空！", Toast.LENGTH_LONG).show();
            loginButtonView.setClickable(true);
            return;
        }
        if (remPasswordView.isChecked()) {
            loginUtil.setUserInfo(LoginUtil.SAVE_PASSWORD, true);
            loginUtil.setUserInfo(LoginUtil.USER_NAME, loginNameViewValue);
            loginUtil.setUserInfo(LoginUtil.USER_PASSWORD, passwordViewValue);
        } else if (!remPasswordView.isChecked()) {
            loginUtil.clear();
        }
        //提示用户正在登陆，不允许其进行操作
        waitDialogFragment = new WaitDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("noteMsg", noteMsg);
        waitDialogFragment.setArguments(bundle);
        waitDialogFragment.setCancelable(false);
        waitDialogFragment.show(getFragmentManager(), "dialogFragment");

        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("login_name", loginNameViewValue)
                .add("login_password", passwordViewValue)
                .build();
        Request request = new Request.Builder()
                .url(Constants.URL_PDA_LOGIN)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("现在的线程是:", Thread.currentThread().getName());
                Message message = new Message();

                Log.i("请求错误原因", e.toString());
                message.what = ERROR_REQUEST;
                Bundle bundle = new Bundle();
                bundle.putString("error_message", e.toString());
                message.setData(bundle);
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("现在的线程是", Thread.currentThread().getName());
                String result = response.body().string();
                Gson gson = new Gson();
                ResultInfo resultInfo = gson.fromJson(result, ResultInfo.class);
                Message message = new Message();
                message.what = SUSSESS_REQUEST;
                Bundle bundle = new Bundle();
                bundle.putSerializable("result_info", resultInfo);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }
        });
    }

    /*
     *初始化缓存，将缓存清空
     */
    public void initCache() {
        DataCach.setPdaLoginMessage(null);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ERROR_REQUEST:
                    Bundle bundle = msg.getData();
                    if (waitDialogFragment.isVisible()) {
                        waitDialogFragment.setCancelable(true);
                        waitDialogFragment.dismiss();
                        waitDialogFragment.onDestroy();
                    }
                    Toast.makeText(LoginActivity.this, "网络连接有误!", Toast.LENGTH_SHORT).show();
                    loginButtonView.setClickable(true);
                    break;
                case SUSSESS_REQUEST:
                    Bundle bundle1 = msg.getData();
                    ResultInfo resultInfo = (ResultInfo) bundle1.getSerializable("result_info");
                    if(resultInfo.getCode().equals(ResultInfo.CODE_GUARDMANIINFO)){
                        if (waitDialogFragment.isVisible()) {
                            waitDialogFragment.setCancelable(true);
                            waitDialogFragment.dismiss();
                            waitDialogFragment.onDestroy();
                        }
                        Toast.makeText(LoginActivity.this, resultInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        loginButtonView.setClickable(true);
                        startActivity(new Intent(LoginActivity.this, HandoverWorkActivity.class));
                    }else if(resultInfo.getCode().equals(ResultInfo.CODE_PEIXIANG)){
                        if (waitDialogFragment.isVisible()) {
                            waitDialogFragment.setCancelable(true);
                            waitDialogFragment.dismiss();
                            waitDialogFragment.onDestroy();
                        }
                        Toast.makeText(LoginActivity.this, resultInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        loginButtonView.setClickable(true);
                        startActivity(new Intent(LoginActivity.this, PeixiangActivity.class));
                        finish();
                    }

                    break;
                default:
                    break;
            }
        }
    };

}
