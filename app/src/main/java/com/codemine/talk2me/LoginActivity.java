package com.codemine.talk2me;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity {

    public static StringBuilder dealResult = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_login);


        checkLogin();

        final LinearLayout logInLayout = (LinearLayout) findViewById(R.id.login_layout);
        final LinearLayout registerLayout = (LinearLayout) findViewById(R.id.register_layout);
        final LinearLayout progressLayout = (LinearLayout) findViewById(R.id.progress_layout);
        final LinearLayout logLayout = (LinearLayout) findViewById(R.id.log_layout);
        final Button loginButton = (Button) findViewById(R.id.login_button);
        final Button registerButton = (Button) findViewById(R.id.register_button);
        final Button loggingButton = (Button) findViewById(R.id.logging_button);
        final EditText accountEdit = (EditText) findViewById(R.id.account_edit);
        final EditText passwordEdit = (EditText) findViewById(R.id.password_edit);

        final EditText registerAccountEdit = (EditText) findViewById(R.id.register_account_edit);
        final EditText registerPasswordEdit = (EditText) findViewById(R.id.register_password_edit);
        final EditText registerConfirmPasswordEdit = (EditText) findViewById(R.id.register_confirm_password_edit);
        final EditText registerEmailEdit = (EditText) findViewById(R.id.register_email_edit);
        final Button registerRegisterButton = (Button) findViewById(R.id.register_register_button);
        final Button registerBackButton = (Button) findViewById(R.id.register_back_button);
        final int passwordInputType = registerPasswordEdit.getInputType();

        //登陆界面
//        progressLayout.setVisibility(View.VISIBLE);
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressLayout.setVisibility(View.VISIBLE);
//            }
//        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                loginButton.setBackgroundColor(Color.rgb(255, 140, 0));
                logLayout.setVisibility(View.GONE);
                loggingButton.setVisibility(View.VISIBLE);

                final String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                dealResult.delete(0, dealResult.length());
                HashMap<String, String> infoMap = new HashMap<>();
                infoMap.put("op", "login");
                infoMap.put("account", account);
                infoMap.put("password", password);
                JSONObject jsonObject = new JSONObject(infoMap);
                new Thread(new SocketOperation(jsonObject, dealResult)).start();
//                while (dealResult.toString().equals(""));

                long startTime = System.currentTimeMillis();
                while (true) {
                    long nowTime = System.currentTimeMillis();
                    if(dealResult.toString().equals("")) {
                        if(nowTime - startTime < 5 * 1000)
                            continue;
                        alert("与服务器断开连接，请重试");
                        progressLayout.setVisibility(View.GONE);
                        break;
                    }
                    else if (dealResult.toString().equals("login success")) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("account", accountEdit.getText().toString());
                        startActivity(intent);
                        finish();
                    } else if (dealResult.toString().equals("error password")) {
                        alert("密码错误");
                    } else if (dealResult.toString().equals("error account")) {
                        alert("账号不存在");
                    } else {
                        alert("系统错误，请重试");
                    }

                    if(!dealResult.toString().equals("")) {
                        progressLayout.setVisibility(View.GONE);
                        break;
                    }
                }

                loggingButton.setVisibility(View.GONE);
                logLayout.setVisibility(View.VISIBLE);
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                registerButton.setBackgroundColor(Color.rgb(0, 100, 0));
//                registerBackButton.setBackgroundColor(Color.rgb(255, 193, 37));
//                registerRegisterButton.setBackgroundColor(Color.rgb(78, 238, 148));
                logInLayout.setVisibility(View.GONE);
                registerLayout.setVisibility(View.VISIBLE);
            }
        });



        //注册界面

        registerAccountEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registerAccountEdit.getText().toString().equals("账号不能为空")
                        || registerAccountEdit.getText().toString().equals("账号已存在")) {
                    registerAccountEdit.getText().clear();
                    registerAccountEdit.setTextColor(Color.BLACK);
                }
            }
        });

        registerPasswordEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registerPasswordEdit.getText().toString().equals("密码不能为空")) {
                    registerPasswordEdit.getText().clear();
                    registerPasswordEdit.setTextColor(Color.BLACK);
                }
                registerPasswordEdit.setInputType(passwordInputType);
            }
        });

        registerConfirmPasswordEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registerConfirmPasswordEdit.getText().toString().equals("确认密码不能为空")
                        || registerConfirmPasswordEdit.getText().toString().equals("两次密码输入需要相同")) {
                    registerConfirmPasswordEdit.getText().clear();
                    registerConfirmPasswordEdit.setTextColor(Color.BLACK);
                }
                registerConfirmPasswordEdit.setInputType(passwordInputType);
            }
        });

        registerEmailEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registerEmailEdit.getText().toString().equals("邮箱地址不能为空")) {
                    registerEmailEdit.getText().clear();
                    registerEmailEdit.setTextColor(Color.BLACK);
                }
            }
        });

        registerBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                loginButton.setBackgroundColor(Color.rgb(255, 193, 37));
//                registerButton.setBackgroundColor(Color.rgb(78, 238, 148));
                logInLayout.setVisibility(View.VISIBLE);
                registerLayout.setVisibility(View.GONE);
            }
        });

        registerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String account = registerAccountEdit.getText().toString();
                String password = registerPasswordEdit.getText().toString();
                String confirmPassword = registerConfirmPasswordEdit.getText().toString();
                String emailAddress = registerEmailEdit.getText().toString();

                if(account.equals("")) {
                    registerAccountEdit.setText("账号不能为空");
                    registerAccountEdit.setTextColor(Color.RED);
                    return;
                }

                if(password.equals("")) {
                    registerPasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                    registerPasswordEdit.setText("密码不能为空");
                    registerPasswordEdit.setTextColor(Color.RED);
                    return;
                }

                if(emailAddress.equals("")) {
                    registerEmailEdit.setText("邮箱地址不能为空");
                    registerEmailEdit.setTextColor(Color.RED);
                    return;
                }

                if(confirmPassword.equals("")) {
                    registerConfirmPasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                    registerConfirmPasswordEdit.setText("确认密码不能为空");
                    registerConfirmPasswordEdit.setTextColor(Color.RED);
                    return;
                }

                if(!password.equals(confirmPassword)) {
                    registerConfirmPasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT);
                    registerConfirmPasswordEdit.setText("两次密码输入需要相同");
                    registerConfirmPasswordEdit.setTextColor(Color.RED);
                    return;
                }

                dealResult = dealResult.delete(0, dealResult.length());
                Map<String, String> map = new HashMap<>();
                map.put("op", "register");
                map.put("account", account);
                map.put("password", password);
                map.put("emailAddress", emailAddress);
                JSONObject jsonObject = new JSONObject(map);
                new Thread(new SocketOperation(jsonObject, dealResult)).start();
                registerRegisterButton.setClickable(false);
                registerRegisterButton.setBackgroundColor(Color.rgb(220, 220, 220));

//                long startTime = System.currentTimeMillis();
//                while(dealResult.toString().equals("")) {
//                    long nowTime = System.currentTimeMillis();
//                    if(nowTime - startTime > 5 * 1000) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                        builder.setMessage("与服务器断开连接，请重试");
//                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                        builder.create().show();
//                        break;
//                    }
//                }

                long startTime = System.currentTimeMillis();
                while(true) {
                    long nowTime = System.currentTimeMillis();
                    if(dealResult.toString().equals("")) {
                        if(nowTime - startTime > 5 * 1000)
                            continue;
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("与服务器断开连接，请重试");
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                        registerRegisterButton.setClickable(true);
                        registerRegisterButton.setBackgroundColor(Color.rgb(78, 238, 148));
                        break;
                    }
                    else if (dealResult.toString().equals("account already exist")) {
                        registerAccountEdit.setText("账号已存在");
                        registerAccountEdit.setTextColor(Color.RED);
                    }
                    else if (dealResult.toString().equals("register success")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("注册成功，请登录");
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                registerAccountEdit.getText().clear();
                                registerPasswordEdit.getText().clear();
                                registerConfirmPasswordEdit.getText().clear();
                                registerEmailEdit.getText().clear();
                                registerLayout.setVisibility(View.GONE);
                                logInLayout.setVisibility(View.VISIBLE);
                            }
                        });
                        builder.create().show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("注册失败，请重试");
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();

                    }

                    if(!dealResult.toString().equals("")) {
                        registerRegisterButton.setClickable(true);
                        registerRegisterButton.setBackgroundColor(Color.rgb(78, 238, 148));
                        break;
                    }
                }
            }
        });
    }

    public void checkLogin() {

    }

    private void alert(String info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(info);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
