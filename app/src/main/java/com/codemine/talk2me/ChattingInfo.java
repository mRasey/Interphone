package com.codemine.talk2me;

public class ChattingInfo {
    protected int otherLinearLayout;
    protected int ownLinearLayout;
    protected int otherHeadPortraitId;
    protected int ownHeadPortraitId;
    protected String otherDialogMsg;
    protected String ownDialogMsg;
    protected MsgType msgType;
    protected String time;

    public ChattingInfo(int ownHeadPortraitId, String ownDialogMsg, MsgType msgType, String time) {
        this.otherLinearLayout = R.id.other_layout;
        this.ownLinearLayout = R.id.own_layout;
        this.ownHeadPortraitId = ownHeadPortraitId;
        this.ownDialogMsg = ownDialogMsg;
        this.msgType = msgType;
        this.time = time;
    }

    public ChattingInfo(int otherLinearLayout, int ownLinearLayout, int otherHeadPortraitId,
                        int ownHeadPortraitId, String otherDialogMsg, String ownDialogMsg, MsgType msgType, String time) {
        this.otherLinearLayout = otherLinearLayout;
        this.ownLinearLayout = ownLinearLayout;
        this.otherHeadPortraitId = otherHeadPortraitId;
        this.ownHeadPortraitId = ownHeadPortraitId;
        this.otherDialogMsg = otherDialogMsg;
        this.ownDialogMsg = ownDialogMsg;
        this.msgType = msgType;
        this.time = time;
    }
}

enum MsgType {
    OTHER,
    OWN
}