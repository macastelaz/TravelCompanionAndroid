package com.castelcode.cruisecompanion.log_entry_add_activity;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

class ProcessImageParamContainer {
    private AppCompatActivity mActivity;
    private Context mContext;
    private EditText mEditText;
    private FloatingActionButton mButton;
    private boolean mSuccess = false;
    private Button mDateSelectButton;
    private Button mTimeSelectButton;

    ProcessImageParamContainer(AppCompatActivity activity, Context context, EditText text,
                               FloatingActionButton button, Button dateSelectButton,
                               Button timeSelectButton){
        mActivity = activity;
        mContext = context;
        mEditText = text;
        mButton = button;
        mDateSelectButton = dateSelectButton;
        mTimeSelectButton = timeSelectButton;
    }

    AppCompatActivity getActivity() { return mActivity; }
    Context getContext() { return mContext; }
    EditText getEditText() { return mEditText; }
    FloatingActionButton getButton() { return mButton; }
    Button getDateSelectButton() { return mDateSelectButton; }
    Button getTimeSelectButton() { return mTimeSelectButton; }

    void setSuccessful(){ mSuccess = true; }
    boolean getSuccess() { return mSuccess; }
}
