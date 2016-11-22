package com.codemine.talk2me;

import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.AudioFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class VoiceActivity extends AppCompatActivity {

    String myIp;
    String oppositeIp;
    Button recordButton;

    //audio
    int frequency = 10000;
    int channelConfiguration = AudioFormat.CHANNEL_IN_DEFAULT;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration,  audioEncoding);
    AudioRecord audioRecord;
    AudioTrack audioTrack = new AudioTrack(
            AudioManager.STREAM_MUSIC,
            frequency,
            channelConfiguration,
            audioEncoding,
            bufferSize*2,
            AudioTrack.MODE_STREAM
    );
    int port = 2333;
    boolean isStop = false;

    private void startSend(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        frequency, channelConfiguration,
                        audioEncoding, bufferSize);
                audioRecord.startRecording();
//                String ip = "127.0.0.1";
                System.out.println("record success");
                try {
                    DatagramSocket datagramSocket = new DatagramSocket();
                    System.out.println("send socket success");
                    byte[] record_buffer = new byte[bufferSize];
                    InetAddress address = InetAddress.getByName(oppositeIp);
                    DatagramPacket datagramPacket = new DatagramPacket(
                            record_buffer,
                            record_buffer.length,
                            address,
                            port);
                    System.out.println("send network success");
                    while(!isStop){
                        audioRecord.read(record_buffer, 0, bufferSize);
                        datagramSocket.send(datagramPacket);
                        System.out.println("send success");
                    }
                    datagramSocket.close();
                    audioRecord.stop();
                    audioRecord.release();
                    isStop = false;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
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

        oppositeIp = getIntent().getStringExtra("ipAddr");//对方ip地址
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                //receive
                System.out.println("receive start");
                try {
                    DatagramSocket datagramSocket = new DatagramSocket(port);
                    byte[] record_buffer = new byte[bufferSize];
                    DatagramPacket datagramPacket = new DatagramPacket(
                            record_buffer,record_buffer.length);
                    System.out.println("receive network success");

                    while(true){
                        datagramSocket.receive(datagramPacket);
                        System.out.println("666666666666");
                        audioTrack.play();
                        audioTrack.write(record_buffer,0,bufferSize);
                        audioTrack.stop();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },"receiver").start();

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
                        startSend();
                        break;
                    case MotionEvent.ACTION_UP:
                        stopSend();
                        break;
                }
                return true;
            }
        });
    }
}
