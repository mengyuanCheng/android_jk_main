package com.grgbanking.ct;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.grgbanking.ct.cach.DataCach;
import com.grgbanking.ct.database.Person;
import com.grgbanking.ct.entity.LoginUser;
import com.grgbanking.ct.http.LoginAsyncTask;
import com.grgbanking.ct.http.ResultInfo;
import com.grgbanking.ct.http.UICallBackDao;
import com.grgbanking.ct.utils.LoginUtil;
import com.grgbanking.ct.utils.StringTools;
import com.grgbanking.ct.utils.WaitDialogFragment;

public class LoginActivity extends Activity implements OnClickListener {

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

        // 将用户名和密码储存在 loginUser实体类中。
        LoginUser loginUser = new LoginUser();
        loginUser.setLoginName(loginNameViewValue);
        loginUser.setPassword(passwordViewValue);
        new LoginAsyncTask(Constants.URL_PDA_LOGIN, loginUser, new UICallBackDao() {
            //TODO 根据返回结果处理UI的一些结束和跳转
            @Override
            public void callBack(ResultInfo resultInfo) {
                Log.e("loginResultinfo------>", resultInfo.getCode() + resultInfo.getMessage());
                if (!TextUtils.isEmpty(resultInfo.getCode()) && resultInfo.getCode().equals(ResultInfo.CODE_ERROR)) {
                    if (waitDialogFragment.isVisible()) {
                        waitDialogFragment.setCancelable(true);
                        waitDialogFragment.dismiss();
                        waitDialogFragment.onDestroy();
                    }
                    Toast.makeText(LoginActivity.this, resultInfo.getMessage(), Toast.LENGTH_LONG).show();
                    loginButtonView.setClickable(true);
                } else if (!TextUtils.isEmpty(resultInfo.getCode()) && resultInfo.getCode().equals(ResultInfo.CODE_PEIXIANG)) {
                    if (waitDialogFragment.isVisible()) {
                        waitDialogFragment.setCancelable(true);
                        waitDialogFragment.dismiss();
                        waitDialogFragment.onDestroy();
                    }
                    Toast.makeText(LoginActivity.this, resultInfo.getMessage(), Toast.LENGTH_SHORT).show();
                    loginButtonView.setClickable(true);
                    startActivity(new Intent(LoginActivity.this, PeixiangActivity.class));
                    finish();
                } else if (!TextUtils.isEmpty(resultInfo.getCode()) && resultInfo.getCode().equals(ResultInfo.CODE_GUARDMANIINFO)) {
                    if (waitDialogFragment.isVisible()) {
                        waitDialogFragment.setCancelable(true);
                        waitDialogFragment.dismiss();
                        waitDialogFragment.onDestroy();
                    }
                    Toast.makeText(LoginActivity.this, resultInfo.getMessage(), Toast.LENGTH_SHORT).show();
                    loginButtonView.setClickable(true);
                    startActivity(new Intent(LoginActivity.this, HandoverWorkActivity.class));
                }
                if (waitDialogFragment.isVisible()) {
                    waitDialogFragment.setCancelable(true);
                    waitDialogFragment.dismiss();
                    waitDialogFragment.onDestroy();
                }
                Toast.makeText(LoginActivity.this, resultInfo.getMessage(), Toast.LENGTH_SHORT).show();
                loginButtonView.setClickable(true);
            }
        }).execute();
    }

    /*
     *初始化缓存，将缓存清空
     */
    public void initCache() {
        DataCach.setPdaLoginMessage(null);
    }

}
