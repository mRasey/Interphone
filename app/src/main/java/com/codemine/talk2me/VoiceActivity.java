package com.codemine.talk2me;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class VoiceActivity extends AppCompatActivity {

    String myIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_voice);

        ((TextView)findViewById(R.id.jump_to_voice_text)).setText("");
        ((TextView)findViewById(R.id.chattingWith)).setText(getIntent().getStringExtra("contactName"));

        String oppositeIp = getIntent().getStringExtra("ipAddr");//对方ip地址
        ((TextView)findViewById(R.id.opposite_ipAddr_text)).setText(oppositeIp);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    myIp = InetAddress.getLocalHost().toString();//本机ip地址
                    ((TextView)findViewById(R.id.my_ipAddr_text)).setText(myIp.substring(10));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        findViewById(R.id.back_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        findViewById(R.id.record_voice_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
