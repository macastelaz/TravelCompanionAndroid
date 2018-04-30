package com.castelcode.cruisecompanion.trip_info_share_activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class CreateEmailParamContainer {

    private AppCompatActivity mActivity;
    private Context mContext;
    private Button mShareButton;

    CreateEmailParamContainer(AppCompatActivity activity, Context context, Button shareButton){
        mActivity = activity;
        mContext = context;
        mShareButton = shareButton;
    }

    public AppCompatActivity getActivity() { return mActivity; }
    public Context getContext() { return mContext; }
    Button getShareButton() { return mShareButton; }

}
