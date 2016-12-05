package com.codemine.talk2me;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
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

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
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
    String oppositeIp;
    ImageButton jumpToVoiceImg;
    Button changeModeButton;
    ChatMode chatMode = ChatMode.TEXT;
    boolean isAlive = true;

    final String multiCastIp = "239.6.7.8";
    boolean isMultiCast;
    int textPort = 2345;

    //audio
    int frequency = 10000;
    int channelConfiguration = AudioFormat.CHANNEL_IN_DEFAULT;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    int bufferSize = 160;
    boolean isStop = false;
    int voicePort = 2333;
    AudioTrack audioTrack = new AudioTrack(
            AudioManager.STREAM_MUSIC,
            frequency,
            channelConfiguration,
            audioEncoding,
            bufferSize*2,
            AudioTrack.MODE_STREAM
    );
    AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                  frequency, channelConfiguration,
                                  audioEncoding, bufferSize);

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
                        new Thread(new VoiceSender()).start();
                        inputVoiceButton.setBackgroundResource(R.color.deepGray);
                        break;
                    case MotionEvent.ACTION_UP:
                        stopSend();
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
        isMultiCast = oppositeIp.equals(multiCastIp);

        //点击返回主界面
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAlive = false;
                audioRecord.release();
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
                        new Thread(new TextSender(jsonObject)).start();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                inputMsgText.getText().clear();
            }
        });

        //循环接收消息
        new Thread(new TextReceiver(),"textReceiver").start();
        new Thread(new VoiceReceiver(),"voiceReceiver").start();
    }

    private class TextReceiver implements Runnable {

        @Override
        public void run() {
            try {
                byte[] text_buffer = new byte[2<<5];
                DatagramSocket datagramSocket;
                InetAddress address = InetAddress.getByName(oppositeIp);
                if(isMultiCast){
                    datagramSocket = new MulticastSocket(textPort);
                    ((MulticastSocket)datagramSocket).joinGroup(address);
                } else {
                    datagramSocket = new DatagramSocket(textPort);
                }
                DatagramPacket datagramPacket = new DatagramPacket(
                        text_buffer,
                        text_buffer.length,
                        address,
                        textPort);
                while (isAlive) {
                    datagramSocket.receive(datagramPacket);
                    if(isMultiCast) {
                        if (datagramPacket.getAddress().
                                toString().substring(1).
                                equals(getLocalIp())) {
                            continue;
                        }
                    } else {
                        if (!datagramPacket.getAddress().
                                toString().substring(1).
                                equals(oppositeIp)) {
                            continue;
                        }
                    }
                    chattingInfos.add(new ChattingInfo(R.drawable.head,new String(text_buffer), MsgType.OTHER, ""));
                    MESSAGE.sendNewMessage(handler, NEW_MSG);
                }
                if(isMultiCast){
                    ((MulticastSocket)datagramSocket).leaveGroup(address);
                }
                datagramSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
                new Thread(new TextReceiver(),"textReceiver").start();
            }
        }
    }

    private class TextSender implements Runnable{
        JSONObject inputJson;

        TextSender(JSONObject inputJson) {
            this.inputJson = inputJson;
        }

        @Override
        public void run() {
            try {
                byte[] text_buffer = new byte[2<<5];
                DatagramSocket datagramSocket = new DatagramSocket();
                InetAddress address = InetAddress.getByName(oppositeIp);
                DatagramPacket datagramPacket = new DatagramPacket(
                        text_buffer,
                        text_buffer.length,
                        address,
                        textPort);

                int i = 0;
                for(byte b: inputJson.getString("info").getBytes()){
                    text_buffer[i++] = b;
                }
                datagramSocket.send(datagramPacket);
                datagramSocket.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                new Thread(new TextSender(inputJson)).start();
            }
        }
    }

    private class VoiceReceiver implements Runnable{
        @Override
        public void run() {
            try {

                byte[] record_buffer = new byte[bufferSize];
                DatagramSocket datagramSocket;
                InetAddress address = InetAddress.getByName(oppositeIp);
                if(isMultiCast){
                    datagramSocket = new MulticastSocket(voicePort);
                    ((MulticastSocket)datagramSocket).joinGroup(address);
                } else {
                    datagramSocket = new DatagramSocket(voicePort);
                }
                DatagramPacket datagramPacket = new DatagramPacket(
                        record_buffer,
                        record_buffer.length,
                        address,
                        voicePort);
                while (isAlive) {
                    datagramSocket.receive(datagramPacket);
                    if(isMultiCast) {
                        if (datagramPacket.getAddress().
                                toString().substring(1).
                                equals(getLocalIp())) {
                            continue;
                        }
                    } else {
                        if (!datagramPacket.getAddress().
                                toString().substring(1).
                                equals(oppositeIp)) {
                            continue;
                        }
                    }
                    audioTrack.play();
                    audioTrack.write(record_buffer,0,bufferSize);
                    audioTrack.stop();
                }
                if(isMultiCast){
                    ((MulticastSocket)datagramSocket).leaveGroup(address);
                }
                datagramSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                new Thread(new VoiceReceiver(),"voiceReceiver").start();
            }

        }
    }

    private class VoiceSender implements Runnable{

        @Override
        public void run() {

            try {
                byte[] record_buffer = new byte[bufferSize];
                DatagramSocket datagramSocket = new DatagramSocket();
                InetAddress address = InetAddress.getByName(oppositeIp);
                DatagramPacket datagramPacket = new DatagramPacket(
                        record_buffer,
                        record_buffer.length,
                        address,
                        voicePort);
                audioRecord.startRecording();
                while(!isStop){
                    audioRecord.read(record_buffer, 0, bufferSize);
                    datagramSocket.send(datagramPacket);
                }
                audioRecord.stop();
                isStop = false;
                datagramSocket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void stopSend(){
        isStop = true;
    }

    @Override
    public void onBackPressed() {
        isAlive = false;
        audioRecord.release();
        finish();
    }

    private String getLocalIp() {
//        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return intToIp(ipAddress);
    }

    private String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
}

enum ChatMode {
    TEXT,
    VOICE
}