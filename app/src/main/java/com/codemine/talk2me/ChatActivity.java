package com.codemine.talk2me;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static com.codemine.talk2me.MESSAGE.*;

public class ChatActivity extends AppCompatActivity {

    ArrayList<ChattingInfo> chattingInfos = new ArrayList<>();
    ListView chatList;
    EditText inputMsgText;
    Button inputVoiceButton;
    Button sendMsgButton;
    TextView backText;
    TextView chattingWith;
    TextView jumpToVoiceText;
    String oppositeIp;
    ServerSocket serverSocket;
    Socket senderSocket;
    Thread receiverThread;
    Thread senderThread;
    ImageButton jumpToVoiceImg;
    Button changeModeButton;
    ChatMode chatMode = ChatMode.TEXT;
    boolean isMultiCast;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NEW_MSG:
                    handler.removeMessages(NEW_MSG);
                    ChattingAdapter chattingAdapter = new ChattingAdapter(ChatActivity.this, R.layout.chatting_item, chattingInfos);
                    chatList.setAdapter(chattingAdapter);
                    chatList.setSelection(chattingInfos.size() - 1);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_chat);

        initChattingInfo();

        chatList = (ListView) findViewById(R.id.chattingListView);
        inputMsgText = (EditText) findViewById(R.id.inputMsgText);
        inputVoiceButton = (Button) findViewById(R.id.inputVoiceButton);
        sendMsgButton = (Button) findViewById(R.id.sendMsgButton);
        backText = (TextView) findViewById(R.id.back_text);
        chattingWith = (TextView) findViewById(R.id.chattingWith);
//        jumpToVoiceText = (TextView) findViewById(R.id.jump_to_voice_text);
        jumpToVoiceImg = (ImageButton) findViewById(R.id.jump_to_voice_img);
        changeModeButton = (Button) findViewById(R.id.changeModButton);//改变模式的按钮

        changeModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chatMode == ChatMode.VOICE) {
                    chatMode = ChatMode.TEXT;
                    inputVoiceButton.setVisibility(View.GONE);
                    inputMsgText.setVisibility(View.VISIBLE);
                    sendMsgButton.setVisibility(View.VISIBLE);
                    changeModeButton.setText("语音");
                    inputMsgText.setHint("请输入......");
                }
                else {
                    chatMode = ChatMode.VOICE;
                    changeModeButton.setText("文字");
                    inputVoiceButton.setVisibility(View.VISIBLE);
                    inputVoiceButton.setBackgroundResource(R.color.white);
                    inputMsgText.setVisibility(View.GONE);
                    sendMsgButton.setVisibility(View.GONE);
//                    inputMsgText.setEnabled(false);
//                    inputMsgText.setText("");
//                    inputMsgText.setHint("按住说话");
//                    inputMsgText.setTextColor(Color.parseColor("#EBEBEB"));
//                    changeModeButton.setText("文字");
                }
            }
        });

        inputVoiceButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        inputVoiceButton.setBackgroundResource(R.color.deepGray);
                        break;
                    case MotionEvent.ACTION_UP:
                        inputVoiceButton.setBackgroundResource(R.color.white);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        chattingWith.setText(getIntent().getStringExtra("contactName"));
        oppositeIp = getIntent().getStringExtra("contactName");
        if(oppositeIp.equals("230.0.0.1")) {
            isMultiCast = true;
        } else {
            isMultiCast = false;
        }

        //点击返回主界面
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    senderThread.interrupt();
                    receiverThread.interrupt();
                    senderThread.stop();
                    receiverThread.stop();
                    senderSocket.close();
                    senderSocket.shutdownInput();
                    senderSocket.shutdownOutput();
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

        //点击跳转到语音通话界面
        jumpToVoiceImg.setOnClickListener(new View.OnClickListener() {
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

        //输入信息
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

        //点击发送消息
        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!inputMsgText.getText().toString().equals("")) {
                    String msg = inputMsgText.getText().toString();
                    chattingInfos.add(new ChattingInfo(R.drawable.head, msg, MsgType.OWN, ""));
                    ChattingAdapter chattingAdapter = new ChattingAdapter(ChatActivity.this, R.layout.chatting_item, chattingInfos);
                    chatList.setAdapter(chattingAdapter);
                    chatList.setSelection(chattingInfos.size() - 1);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("info", msg);
                        senderThread = new Thread(new Sender(jsonObject));
                        senderThread.start();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                inputMsgText.getText().clear();
            }
        });

        //循环接收消息

        Receiver receiver = new Receiver();
        receiverThread = new Thread(receiver);
        receiverThread.start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while (true) {
//                        ServerSocket serverSocket = new ServerSocket(2345);
//                        Socket socket = serverSocket.accept();
//                        BufferedReader bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                        chattingInfos.add(new ChattingInfo(R.drawable.head, bfr.readLine(), MsgType.OTHER, ""));
//                        MESSAGE.sendNewMessage(handler, NEW_MSG);
//                        serverSocket.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }).start();
    }


    private class Receiver implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    serverSocket = new ServerSocket(2345);
                    Socket socket = serverSocket.accept();
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    chattingInfos.add(new ChattingInfo(R.drawable.head, bfr.readLine(), MsgType.OTHER, ""));
                    MESSAGE.sendNewMessage(handler, NEW_MSG);
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                receiverThread = new Thread(new Receiver());
                receiverThread.start();
            }
        }
    }

    private class Sender implements Runnable{
        JSONObject inputJson;

        public Sender(JSONObject inputJson) {
            this.inputJson = inputJson;
        }

        @Override
        public void run() {
            try {
                senderSocket = new Socket(oppositeIp, 2345);
                BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(senderSocket.getOutputStream()));
                BufferedReader bfr = new BufferedReader(new InputStreamReader(senderSocket.getInputStream()));
                bfw.write(inputJson.getString("info") + "\n");
                bfw.flush();
                senderSocket.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                new Thread(new Sender(inputJson)).start();
            }
        }
    }

    @Override
    public void onBackPressed() {
        try {
            senderThread.interrupt();
            receiverThread.interrupt();
            senderThread.stop();
            receiverThread.stop();
            senderSocket.close();
            senderSocket.shutdownInput();
            senderSocket.shutdownOutput();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }

    public void initChattingInfo() {

    }
}

enum ChatMode {
    TEXT,
    VOICE
}