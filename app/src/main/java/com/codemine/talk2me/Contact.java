package com.codemine.talk2me;

import android.widget.ImageView;

public class Contact {
    protected String name;
    protected String msg;
    protected String time;
    protected int headPortraitId;

    public Contact(String name, String msg, String time, int headPortraitId) {
        this.name = name;
        this.msg = msg;
        this.time = time;
        this.headPortraitId = headPortraitId;
    }
}
