package com.codemine.talk2me;

import android.graphics.Color;
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
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import static com.codemine.talk2me.MESSAGE.NEW_MSG;

public class VoiceActivity extends AppCompatActivity {

    String myIp;
    String oppositeIp;
    ImageButton recordButton;
    TextView connect_text;

    //audio
    int frequency = 10000;
    int channelConfiguration = AudioFormat.CHANNEL_IN_DEFAULT;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
//    int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration,  audioEncoding);
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
    int port = 2333;
    boolean isStop = false;

    private class receiver implements Runnable{
        @Override
        public void run() {
            try {
                while (true) {
                    byte[] bytes = new byte[bufferSize];
                    ServerSocket serverSocket = new ServerSocket(port);
                    Socket socket = serverSocket.accept();
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    while(dis.read(bytes) != -1) {
                        audioTrack.play();
                        audioTrack.write(bytes,0,bufferSize);
                        audioTrack.stop();
                    }
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                new Thread(new receiver()).start();
            }

        }
    }

    private void startSend(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        frequency, channelConfiguration,
                        audioEncoding, bufferSize);
                audioRecord.startRecording();
//                String ip = "127.0.0.1";
                try {
                    Socket socket = new Socket(oppositeIp, port);
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

//                    DatagramSocket datagramSocket = new DatagramSocket();
                    byte[] record_buffer = new byte[bufferSize];
                    InetAddress address = InetAddress.getByName(oppositeIp);
                    DatagramPacket datagramPacket = new DatagramPacket(
                            record_buffer,
                            record_buffer.length,
                            address,
                            port);
                    while(!isStop){
                        audioRecord.read(record_buffer, 0, bufferSize);
                        dos.write(record_buffer);
                        dos.flush();
//                        datagramSocket.send(datagramPacket);
                    }
//                    datagramSocket.close();
                    audioRecord.stop();
                    audioRecord.release();
                    isStop = false;
                    socket.close();
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

        findViewById(R.id.jump_to_voice_img).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.chattingWith)).setText(getIntent().getStringExtra("contactName"));

        connect_text = (TextView) findViewById(R.id.bluetoothConnect);
        connect_text.setVisibility(View.GONE);

        oppositeIp = getIntent().getStringExtra("ipAddr");//对方ip地址
        ((TextView)findViewById(R.id.opposite_ipAddr_text)).setText(oppositeIp);

        System.out.println("buffersize : "  + bufferSize);

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

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //receive
//                System.out.println("receive start");
//                try {
//                    DatagramSocket datagramSocket = new DatagramSocket(port);
//                    byte[] record_buffer = new byte[bufferSize];
//                    DatagramPacket datagramPacket = new DatagramPacket(
//                            record_buffer,record_buffer.length);
//                    System.out.println("receive network success");
//
//                    while(true){
//                        datagramSocket.receive(datagramPacket);
//                        System.out.println("666666666666");
//                        audioTrack.play();
//                        audioTrack.write(record_buffer,0,bufferSize);
//                        audioTrack.stop();
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        },"receiver").start();

        //循环接收消息
        new Thread(new receiver()).start();

        findViewById(R.id.back_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recordButton = (ImageButton) findViewById(R.id.record_button);
        //录音按钮添加长按事件
        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        recordButton.setBackgroundResource(R.drawable.start);
                        startSend();
                        break;
                    case MotionEvent.ACTION_UP:
                        recordButton.setBackgroundResource(R.drawable.stop);
                        stopSend();
                        break;
                }
                return true;
            }
        });
    }
}
