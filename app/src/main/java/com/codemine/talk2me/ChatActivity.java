package com.codemine.talk2me;

import android.content.Intent;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    ArrayList<ChattingInfo> chattingInfos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_chat);

        initChattingInfo();

        final ListView chatList = (ListView) findViewById(R.id.chattingListView);
        final EditText inputMsgText = (EditText) findViewById(R.id.inputMsgText);
        final Button sendMsgButton = (Button) findViewById(R.id.sendMsgButton);
        final TextView backText = (TextView) findViewById(R.id.back_text);
        final TextView chattingWith = (TextView) findViewById(R.id.chattingWith);
        final TextView jumpToVoiceText = (TextView) findViewById(R.id.jump_to_voice_text);

        chattingWith.setText(getIntent().getStringExtra("contactName"));

        //点击返回主界面
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //点击跳转到语音通话界面
        jumpToVoiceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("contactName", getIntent().getStringExtra("contactName"));
                intent.putExtra("ipAddr", getIntent().getStringExtra("ipAddr"));
                intent.setClass(ChatActivity.this, VoiceActivity.class);
                startActivity(intent);
            }
        });

        ChattingAdapter chattingAdapter = new ChattingAdapter(ChatActivity.this, R.layout.chatting_item, chattingInfos);
        chatList.setAdapter(chattingAdapter);
        chatList.setSelection(chattingInfos.size() - 1);

        //点击发送信息
        inputMsgText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!inputMsgText.getText().toString().equals(""))
                    sendMsgButton.setBackgroundColor(Color.parseColor("#FFC125"));
                else
                    sendMsgButton.setBackgroundColor(Color.parseColor("#EBEBEB"));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!inputMsgText.getText().toString().equals("")) {
                    chattingInfos.add(new ChattingInfo(R.drawable.head, inputMsgText.getText().toString(), MsgType.OWN, ""));
                    ChattingAdapter chattingAdapter = new ChattingAdapter(ChatActivity.this, R.layout.chatting_item, chattingInfos);
                    chatList.setAdapter(chattingAdapter);
                    chatList.setSelection(chattingInfos.size() - 1);
                }
                inputMsgText.getText().clear();
            }
        });
    }

    public void initChattingInfo() {
//        chattingInfos.add(new ChattingInfo(R.id.other_layout, R.id.own_layout, R.drawable.head,
//                R.drawable.head, "hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello", "", MsgType.OTHER, "now"));
//        chattingInfos.add(new ChattingInfo(R.id.other_layout, R.id.own_layout, R.drawable.head,
//                R.drawable.head, "", "worldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworldworld", MsgType.OWN, "now"));
    }
}