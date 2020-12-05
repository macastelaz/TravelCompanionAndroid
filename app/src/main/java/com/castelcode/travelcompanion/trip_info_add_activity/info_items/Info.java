package com.castelcode.travelcompanion.trip_info_add_activity.info_items;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


public class Info implements Parcelable, Serializable{
    String mPrimaryName;
    String mConfirmationNumber;
    String mPhoneNumber;
    String mStartDate;
    String mStartTime;

    public Info(String primaryName, String confirmationNumber, String phoneNumber, String startDate,
                String startTime){
        mPrimaryName = primaryName;
        mConfirmationNumber = confirmationNumber;
        mPhoneNumber = phoneNumber;
        mStartDate = startDate;
        mStartTime = startTime;
    }

    public Info(){

    }

    private Info(Parcel in) {
        mPrimaryName = in.readString();
        mConfirmationNumber = in.readString();
        mPhoneNumber = in.readString();
        mStartDate = in.readString();
        mStartTime = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPrimaryName);
        dest.writeString(mConfirmationNumber);
        dest.writeString(mPhoneNumber);
        dest.writeString(mStartDate);
        dest.writeString(mStartTime);
    }

    public static final Creator<Info> CREATOR = new Creator<Info>() {
        @Override
        public Info createFromParcel(Parcel in) {
            return new Info(in);
        }

        @Override
        public Info[] newArray(int size) {
            return new Info[size];
        }
    };

    public String getPrimaryName(){
        return mPrimaryName;
    }

    public String getConfirmationNumber(){
        return mConfirmationNumber;
    }

    public String getPhoneNumber(){
        return mPhoneNumber;
    }

    public String getStartDate() { return mStartDate; }

    public String getStartTime() { return mStartTime; }

    @Override
    public boolean equals(Object o){
        if(o != null && o instanceof Info){
            Info other = (Info)o;
            return this.mPrimaryName.equals(other.getPrimaryName()) &&
                    this.mConfirmationNumber.equals(other.getConfirmationNumber());
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toShareableString() {
        return "UNIMPLEMENTED";
    }
}
