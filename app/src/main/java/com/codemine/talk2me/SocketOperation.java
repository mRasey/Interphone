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

    public void setDealResult(StringBuilder dealResult) {
        this.dealResult = dealResult;
    }

    public void setInputJson(JSONObject inputJson) {
        this.inputJson = inputJson;
    }

    public void setCallBackJson(JSONObject callBackJson) {
        this.callBackJson = callBackJson;
    }

    public String sendMsg() throws IOException {
        Socket socket = new Socket("192.168.31.132", 2333);
        String ipAddress = socket.getInetAddress().toString();
        BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bfw.write(inputJson.toString() + "\n");
        bfw.flush();
        dealResult.append(bfr.readLine());
        return dealResult.toString();
    }

    public JSONObject getMsg() throws IOException, JSONException {
        Socket socket = new Socket("192.168.31.132", 2333);
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
