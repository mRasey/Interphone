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

    Button WIFIButton;
    Button bluetoothButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        WIFIButton = (Button) findViewById(R.id.WIFI_button);
        bluetoothButton = (Button) findViewById(R.id.bluetooth_button);

        WIFIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                intent.putExtra("mod", "WIFI");
                startActivity(intent);
                finish();
            }
        });

        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, BluetoothActivity.class);
                intent.putExtra("mod", "bluetooth");
                startActivity(intent);
                finish();
            }
        });

    }
}
