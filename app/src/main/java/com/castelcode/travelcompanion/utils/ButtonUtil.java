package com.castelcode.travelcompanion.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;

import com.castelcode.travelcompanion.R;

public class ButtonUtil {
    @SuppressLint("ClickableViewAccessibility")
    public static void buttonEffect(View button, final Context context){
        button.setOnTouchListener((View v, MotionEvent event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    v.getBackground().setColorFilter(ContextCompat.getColor(context,
                            R.color.spinnerColor), PorterDuff.Mode.SRC_ATOP);
                    v.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    v.getBackground().clearColorFilter();
                    v.invalidate();
                    break;
                }
            }
            return false;
        });
    }
}
