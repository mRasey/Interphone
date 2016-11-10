package com.codemine.talk2me;

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

        chattingWith.setText(getIntent().getStringExtra("contactName"));

        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ChattingAdapter chattingAdapter = new ChattingAdapter(ChatActivity.this, R.layout.chatting_item, chattingInfos);
        chatList.setAdapter(chattingAdapter);
        chatList.setSelection(chattingInfos.size() - 1);


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