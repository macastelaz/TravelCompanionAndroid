package com.castelcode.cruisecompanion.log_entry_add_activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;

public class CreateEmailParamContainer {

    private AppCompatActivity mActivity;
    private Context mContext;
    private int mItemPosition;
    private Button mShareButton;
    private ArrayList<LogEntry> mEntries;
    private File mFileDir;

    public CreateEmailParamContainer(AppCompatActivity activity, Context context, int itemPos,
                                     Button shareButton, ArrayList<LogEntry> entries, File fileDir){
        mActivity = activity;
        mContext = context;
        mItemPosition = itemPos;
        mShareButton = shareButton;
        mEntries = entries;
        mFileDir = fileDir;
    }

    public AppCompatActivity getActivity() { return mActivity; }
    public Context getContext() { return mContext; }
    public int getItemPosition() { return mItemPosition; }
    public Button getShareButton() { return mShareButton; }
    public ArrayList<LogEntry> getEntries() { return mEntries; }
    public File getFileDir() { return mFileDir; }

}
