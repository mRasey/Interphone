package com.codemine.talk2me;

import android.widget.ImageView;

public class Contact {
    protected String name;
    protected String msg;
    protected String time;
    protected int headPortraitId;
    private String addr;

    public Contact(String name, String msg, String time, int headPortraitId) {
        this.name = name;
        this.msg = msg;
        this.time = time;
        this.headPortraitId = headPortraitId;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getAddr() {
        return addr;
    }

    public String getName() {
        return name;
    }

    public Contact(String name) {
        this.name = name;
    }
}
