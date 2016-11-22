package com.codemine.talk2me;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketOperation implements Runnable{

    private StringBuilder dealResult;
    private JSONObject inputJson;
    private JSONObject callBackJson;
    private String ip = "";

    public SocketOperation(JSONObject jsonObject, StringBuilder dealResult) {
        this.inputJson = jsonObject;
        this.dealResult = dealResult;
    }

    public SocketOperation(JSONObject jsonObject, JSONObject callBackJson) {
        this.inputJson = jsonObject;
        this.callBackJson = callBackJson;
    }

    public SocketOperation(JSONObject inputJson) {
        this.inputJson = inputJson;
    }

    public SocketOperation(JSONObject jsonObject, String oppositeIp) {
        this.inputJson = jsonObject;
        this.ip = oppositeIp;
    }

    public void setDealResult(StringBuilder dealResult) {
        this.dealResult = dealResult;
    }

    public void setInputJson(JSONObject inputJson) {
        this.inputJson = inputJson;
    }

    public void setCallBackJson(JSONObject callBackJson) {
        this.callBackJson = callBackJson;
    }

    public void sendMsg() throws IOException {
        try {
            Socket socket = new Socket(ip, 2345);
            BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bfw.write(inputJson.getString("info") + "\n");
            bfw.flush();
            socket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
//        dealResult.append(bfr.readLine());
//        return dealResult.toString();
    }

    public JSONObject getMsg() throws IOException, JSONException {
        Socket socket = new Socket(ip, 2345);
        String ipAddress = socket.getInetAddress().toString();
        BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bfw.write(inputJson.toString() + '\n');
        bfw.flush();
        callBackJson = new JSONObject(bfr.readLine());
        return callBackJson;
    }

    @Override
    public void run() {
        try {
            sendMsg();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
