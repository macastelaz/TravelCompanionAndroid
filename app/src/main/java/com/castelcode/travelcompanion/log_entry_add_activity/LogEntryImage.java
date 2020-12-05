package com.castelcode.travelcompanion.log_entry_add_activity;

import java.io.Serializable;

public class LogEntryImage implements Serializable{
    String mPhotoPath;
    String mPhotoName;

    LogEntryImage(String photoPath, String photoName) {
        mPhotoPath = photoPath;
        mPhotoName = photoName;
    }

    public void setPhotoPath(String photoPath) {
        mPhotoPath = photoPath;
    }

    public String getPhotoPath() {
        return mPhotoPath;
    }

    public void setPhotoName(String photoName) {
        mPhotoName = photoName;
    }

    public String getPhotoName() {
        return mPhotoName;
    }

}
