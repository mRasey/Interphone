package com.codemine.talk2me;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codemine.talk2me.bluetoothchat.BluetoothChatService;
import com.codemine.talk2me.bluetoothchat.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class BluetoothChatActivity extends AppCompatActivity {
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    ArrayList<ChattingInfo> chattingInfos = new ArrayList<>();
    private BluetoothChatService mChatService;
    private String mConnectedDeviceName;
    private BluetoothAdapter mBtAdapter;
    private ChattingAdapter chattingAdapter;
    private ListView chatList;
    private EditText inputMsgText;
    private Button sendMsgButton;
    private TextView backText;
    private TextView chattingWith;
    private TextView jumpToVoiceText;
    private TextView bloothConnect;
    private BluetoothDevice device;
    Button changeModeButton;
    ChatMode chatMode = ChatMode.TEXT;
    Button inputVoiceButton;

    int frequency = 10000;
    int channelConfiguration = AudioFormat.CHANNEL_IN_DEFAULT;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    int bufferSize = 128*1024;
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
    boolean isStop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_chat);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();



        mChatService = new BluetoothChatService(mHandler);
        Thread t = new Thread(mChatService);
        t.start();

        mChatService.start();

        String address = getIntent().getStringExtra("device address");
        // Get the BluetoothDevice object
        device = mChatService.mAdapter.getRemoteDevice(address);
        HashMap<String, Object> param = new HashMap<String, Object>();
        param.put("mHandler", mHandler);
        param.put("device", device);
        param.put("secure", true);
        Task task = new Task(Task.TASK_CONECT_DEVICE, param);
        mChatService.newTask(task);

        // Attempt to connect to the device

        ((Data) getApplication()).setmChatService(mChatService);
        chatList = (ListView) findViewById(R.id.chattingListView);
        inputMsgText = (EditText) findViewById(R.id.inputMsgText);
        sendMsgButton = (Button) findViewById(R.id.sendMsgButton);
        bloothConnect = (TextView) findViewById(R.id.bluetoothConnect);
        backText = (TextView) findViewById(R.id.back_text);
        chattingWith = (TextView) findViewById(R.id.chattingWith);
        jumpToVoiceText = (TextView) findViewById(R.id.jump_to_voice_text);

        chattingWith.setText(getIntent().getStringExtra("device name"));
        mConnectedDeviceName = getIntent().getStringExtra("device name");
        //点击返回主界面
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        inputVoiceButton = (Button) findViewById(R.id.inputVoiceButton);
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
                        startSend();
                        break;
                    case MotionEvent.ACTION_UP:
                        inputVoiceButton.setBackgroundResource(R.color.white);
                        stopSend();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        //点击跳转到语音通话界面
//        jumpToVoiceText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.putExtra("contactName", getIntent().getStringExtra("device name"));
//                intent.putExtra("ipAddr", getIntent().getStringExtra("device address"));
//                intent.setClass(BluetoothChatActivity.this, BluetoothVoiceActivity.class);
//                startActivity(intent);
//            }
//        });
        bloothConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> param = new HashMap<String, Object>();
                param.put("mHandler", mHandler);
                param.put("device", device);
                param.put("secure", true);
                Task task = new Task(Task.TASK_CONECT_DEVICE, param);
                mChatService.newTask(task);
            }
        });
        ChattingAdapter chattingAdapter = new ChattingAdapter(BluetoothChatActivity.this, R.layout.chatting_item, chattingInfos);
        chatList.setAdapter(chattingAdapter);
        chatList.setSelection(chattingInfos.size() - 1);

        //点击发送信息
        inputMsgText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!inputMsgText.getText().toString().equals(""))
                    sendMsgButton.setBackgroundColor(Color.parseColor("#FFC125"));
                else
                    sendMsgButton.setBackgroundColor(Color.parseColor("#EBEBEB"));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        chattingAdapter = new ChattingAdapter(BluetoothChatActivity.this, R.layout.chatting_item, chattingInfos);
        chatList.setAdapter(chattingAdapter);
        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inputMsgText.getText().toString().equals("")) {
                    String message = inputMsgText.getText().toString();
                    sendMessage(message);
                }
                inputMsgText.getText().clear();
            }
        });
    }

    public void writebyte(byte[] buffer, String message){
        System.out.println(message);

        for(byte b: buffer)
            System.out.print(b);
        System.out.println("23333333333");
    }

    private void startSend(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                new AudioRecord(MediaRecorder.AudioSource.MIC,
                        frequency, channelConfiguration,
                        audioEncoding, bufferSize);
                audioRecord.startRecording();
                byte[] record_buffer = new byte[bufferSize];
                while(!isStop){
                    audioRecord.read(record_buffer, 0, bufferSize);
                    writebyte(record_buffer,"send");
                    sendMessage(record_buffer);
                }
                audioRecord.stop();
                isStop = false;
            }
        }).start();
    }
    private void sendMessage(byte[] message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            //Toast.makeText(BluetoothVoiceActivity.this, "not connected!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (message.length > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            HashMap<Integer, byte[]> param = new HashMap<Integer, byte[]>();
            param.put(BluetoothChatService.STATE_SEND_MESSAGE, message);
            Task task = new Task(Task.TASK_SEND_MESSAGE, param);
            BluetoothChatService.newTask(task);
        }
    }
    private void stopSend(){
        isStop = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mChatService.setmHandler(mHandler);
    }
    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(BluetoothChatActivity.this, "not connected!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            HashMap<Integer, byte[]> param = new HashMap<Integer, byte[]>();
            param.put(BluetoothChatService.STATE_SEND_MESSAGE, send);
            Task task = new Task(Task.TASK_SEND_MESSAGE, param);
            BluetoothChatService.newTask(task);
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
//                    switch (msg.arg1) {
//                        case BluetoothChatService.STATE_CONNECTED:
//                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
//                            chattingAdapter.clear();
//                            break;
//                        case BluetoothChatService.STATE_CONNECTING:
//                            setStatus(R.string.title_connecting);
//                            break;
//                        case BluetoothChatService.STATE_LISTEN:
//                        case BluetoothChatService.STATE_NONE:
//                            setStatus(R.string.title_not_connected);
//                            break;
//                    }
                    break;
                case Constants.MESSAGE_WRITE:{
                    if(chatMode==ChatMode.TEXT){
                        byte[] writeBuf = (byte[]) msg.obj;
                        // construct a string from the buffer
                        String writeMessage = new String(writeBuf);
                        chattingInfos.add(new ChattingInfo(R.drawable.head, writeMessage, MsgType.OWN, ""));
                        chatList.setSelection(chattingInfos.size() - 1);

                    }else{
//                        byte[] readBuf = (byte[]) msg.obj;
//                        writebyte(readBuf,"read");
                        // construct a string from the valid bytes in the buffer
//                        audioTrack.play();
//                        audioTrack.write(readBuf,0,bufferSize);
//                        audioTrack.stop();

                        chattingInfos.add(new ChattingInfo(R.drawable.head, "语音发送成功", MsgType.OWN, ""));
                        chatList.setSelection(chattingInfos.size() - 1);
                    }
                    break;
                }
                case Constants.MESSAGE_READ:{
                    System.out.println("read state");
                    if(chatMode==ChatMode.VOICE){
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        audioTrack.play();
                        audioTrack.write(readBuf,0,bufferSize);
                        audioTrack.stop();

                    }else{
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        chattingInfos.add(new ChattingInfo(R.drawable.head, readMessage, MsgType.OTHER, ""));
                        chatList.setSelection(chattingInfos.size() - 1);
                    }
                    break;
                }
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(BluetoothChatActivity.this, "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(BluetoothChatActivity.this, msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}