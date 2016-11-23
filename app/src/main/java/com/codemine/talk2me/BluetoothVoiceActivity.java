package com.codemine.talk2me;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.AudioFormat;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codemine.talk2me.bluetoothchat.BluetoothChatService;
import com.codemine.talk2me.bluetoothchat.Constants;

import static com.codemine.talk2me.MESSAGE.NEW_MSG;

public class BluetoothVoiceActivity extends AppCompatActivity {
    Button recordButton;
    //audio
    int frequency = 10000;
    int channelConfiguration = AudioFormat.CHANNEL_IN_DEFAULT;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    int bufferSize = 128;
    AudioRecord audioRecord;
    AudioTrack audioTrack = new AudioTrack(
            AudioManager.STREAM_MUSIC,
            frequency,
            channelConfiguration,
            audioEncoding,
            bufferSize*2,
            AudioTrack.MODE_STREAM
    );
    boolean isStop = false;
    private BluetoothChatService mChatService;

    private void startSend(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        frequency, channelConfiguration,
                        audioEncoding, bufferSize);
                audioRecord.startRecording();
                byte[] record_buffer = new byte[bufferSize];
                while(!isStop){
                    audioRecord.read(record_buffer, 0, bufferSize);
                    sendMessage(record_buffer);
                }
                audioRecord.stop();
                audioRecord.release();
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
        mChatService.write(message);
    }
    private void stopSend(){
        isStop = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_voice);

        ((TextView)findViewById(R.id.jump_to_voice_text)).setText("");
        ((TextView)findViewById(R.id.chattingWith)).setText(getIntent().getStringExtra("contactName"));

        mChatService=((Data) getApplication()).getmChatService();


        findViewById(R.id.back_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recordButton = (Button) findViewById(R.id.record_voice_button);
        //录音按钮添加长按事件
        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        recordButton.setBackgroundColor(Color.parseColor("#4EEE94"));
                        startSend();
                        break;
                    case MotionEvent.ACTION_UP:
                        recordButton.setBackgroundColor(Color.parseColor("#EBEBEB"));
                        stopSend();
                        break;
                }
                return true;
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mChatService.setmHandler(mHandler);
    }
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    audioTrack.play();
                    audioTrack.write(readBuf,0,bufferSize);
                    audioTrack.stop();
                    break;
            }
        }
    };
}
