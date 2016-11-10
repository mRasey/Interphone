package com.codemine.talk2me;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ContactsAdapter extends ArrayAdapter<Contact> {
    private int resourceId;

    public ContactsAdapter(Context context, int textViewResourceId, List<Contact> objects) {
        super(context, textViewResourceId, objects);
        this.resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact contact = getItem(position);
        View view;
        ContactsViewHolder viewHolder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ContactsViewHolder();
            viewHolder.headPortrait = (ImageView) view.findViewById(R.id.headPortrait);
            viewHolder.contactName = (TextView) view.findViewById(R.id.contactName);
            viewHolder.recentTime = (TextView) view.findViewById(R.id.recentTime);
            viewHolder.recentMsg = (TextView) view.findViewById(R.id.recentMsg);
            view.setTag(viewHolder);
        }
        else {
            view = convertView;
            viewHolder = (ContactsViewHolder) view.getTag();
        }
        viewHolder.headPortrait.setImageResource(contact.headPortraitId);
        viewHolder.contactName.setText(contact.name);
        viewHolder.recentTime.setText(contact.time);
        viewHolder.recentMsg.setText(contact.msg);
        return view;
    }
}

class ContactsViewHolder {
    ImageView headPortrait;
    TextView contactName;
    TextView recentTime;
    TextView recentMsg;
}