package com.codemine.talk2me;

import android.content.DialogInterface;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

public class ButtonListener implements DialogInterface.OnClickListener, View.OnTouchListener{
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view.getId() == R.id.login_button) {
            if(motionEvent.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                view.setBackgroundColor(Color.RED);
            }
            if(motionEvent.getAction() == MotionEvent.ACTION_BUTTON_RELEASE) {
                view.setBackgroundColor(Color.parseColor(String.valueOf(R.color.orange)));
            }
        }
        return false;
    }
}
