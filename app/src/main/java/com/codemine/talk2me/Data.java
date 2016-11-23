package com.codemine.talk2me;

import android.app.Application;

import com.codemine.talk2me.bluetoothchat.BluetoothChatService;

/**
 * Created by Kevin on 2016/11/20.
 */

public class Data extends Application {
    private BluetoothChatService mChatService = null;

    public BluetoothChatService getmChatService() {
        return mChatService;
    }

    public void setmChatService(BluetoothChatService mChatService) {
        this.mChatService = mChatService;
    }
}
