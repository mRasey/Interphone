package com.codemine.talk2me;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ChattingAdapter extends ArrayAdapter<ChattingInfo>{
    private int resourceId;


    public ChattingAdapter(Context context, int resourceId, List<ChattingInfo> objects) {
        super(context, resourceId, objects);
        this.resourceId = resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChattingInfo chattingInfo = getItem(position);
        View view;
        ChatViewHolder chatViewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            chatViewHolder = new ChatViewHolder();
            chatViewHolder.otherLinearLayout = (LinearLayout) view.findViewById(R.id.other_layout);
            chatViewHolder.otherHeadPortrait = (ImageView) view.findViewById(R.id.other_head_portrait);
            chatViewHolder.otherDialogMsg = (TextView) view.findViewById(R.id.other_dialog_msg);
            chatViewHolder.ownLinearLayout = (LinearLayout) view.findViewById(R.id.own_layout);
            chatViewHolder.ownHeadPortrait = (ImageView) view.findViewById(R.id.own_head_portrait);
            chatViewHolder.ownDialogMsg = (TextView) view.findViewById(R.id.own_dialog_msg);
            view.setTag(chatViewHolder);
        }
        else {
            view = convertView;
            chatViewHolder = (ChatViewHolder) view.getTag();
        }
        if(chattingInfo.msgType == MsgType.OTHER) {
            chatViewHolder.ownLinearLayout.setVisibility(View.GONE);
            chatViewHolder.otherLinearLayout.setVisibility(View.VISIBLE);

            chatViewHolder.otherHeadPortrait.setImageResource(chattingInfo.ownHeadPortraitId);
            chatViewHolder.otherDialogMsg.setText(chattingInfo.ownDialogMsg);
        }
        else if(chattingInfo.msgType == MsgType.OWN){
            chatViewHolder.ownLinearLayout.setVisibility(View.VISIBLE);
            chatViewHolder.otherLinearLayout.setVisibility(View.GONE);

            chatViewHolder.ownHeadPortrait.setImageResource(chattingInfo.ownHeadPortraitId);
            chatViewHolder.ownDialogMsg.setText(chattingInfo.ownDialogMsg);
        }
        return view;
    }

}

class ChatViewHolder {
    LinearLayout otherLinearLayout;
    LinearLayout ownLinearLayout;
    ImageView otherHeadPortrait;
    TextView otherDialogMsg;
    ImageView ownHeadPortrait;
    TextView ownDialogMsg;
}
