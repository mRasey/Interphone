package com.codemine.talk2me;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class VoiceActivity extends AppCompatActivity {

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
        try {
            String myIp = InetAddress.getLocalHost().toString();//本机ip地址
            ((TextView)findViewById(R.id.my_ipAddr_text)).setText(myIp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        findViewById(R.id.back_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
