package com.codemine.talk2me;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = LauncherActivity.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        setContentView(R.layout.activity_launcher);

//        startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
//        finish();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
                finish();
            }
        }, 3000);
    }
}
