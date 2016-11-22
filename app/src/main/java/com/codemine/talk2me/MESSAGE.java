package com.codemine.talk2me;


import android.os.Handler;
import android.os.Message;

/**
 * Created by billy on 2016/11/21.
 */

public class MESSAGE {
    final static int NEW_MSG = 1;

    public static void sendNewMessage(Handler handler, int what) {
        Message message = new Message();
        message.what = what;
        handler.sendMessage(message);
    }
}
