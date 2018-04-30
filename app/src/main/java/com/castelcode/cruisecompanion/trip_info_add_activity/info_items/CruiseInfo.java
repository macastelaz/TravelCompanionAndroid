package com.castelcode.cruisecompanion.trip_info_add_activity.info_items;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;

import java.io.Serializable;

public class CruiseInfo extends Info implements Parcelable, Serializable {

    @SuppressWarnings("unused")
    private final String type = "Cruise";
    public static final String typeOfItem = "Cruise";

    private String mRoomNumber;
    private String mShipName;

    public CruiseInfo(String primaryName, String confirmationNumber, String phoneNumber,
                      String roomNumber, String shipName, String departureDate,
                      String departureTime){
        super(primaryName, confirmationNumber, phoneNumber, departureDate, departureTime);
        mRoomNumber = roomNumber;
        mShipName = shipName;
    }

    public CruiseInfo(String shareableString){
        String [] parts = shareableString.split("\\|");
        if(parts.length < 8)
            return;
        mPrimaryName = parts[1];
        mConfirmationNumber = parts[2];
        mPhoneNumber = parts[3];
        mStartDate = parts[4];
        mStartTime = parts[5];
        mRoomNumber = parts[6];
        mShipName = parts[7];
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public CruiseInfo(Parcel in) {
        mPrimaryName = in.readString();
        mConfirmationNumber = in.readString();
        mPhoneNumber = in.readString();
        mStartDate = in.readString();
        mStartTime = in.readString();
        mRoomNumber = in.readString();
        mShipName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPrimaryName);
        dest.writeString(mConfirmationNumber);
        dest.writeString(mPhoneNumber);
        dest.writeString(mStartDate);
        dest.writeString(mStartTime);
        dest.writeString(mRoomNumber);
        dest.writeString(mShipName);
    }
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Info> CREATOR = new Creator<Info>() {
        @Override
        public Info createFromParcel(Parcel in) {
            return new CruiseInfo(in);
        }

        @Override
        public Info[] newArray(int size) {
            return new Info[size];
        }
    };

    public String getRoomNumber(){
        return mRoomNumber;
    }

    public String getShipName(){
        return mShipName;
    }

    @Override
    public String toString(){
        return mPrimaryName + " "  + mShipName + " - " + mConfirmationNumber;
    }

    @Override
    public String toShareableString() {
        String SEPARATOR = "|";
        return typeOfItem + SEPARATOR + mPrimaryName + SEPARATOR + mConfirmationNumber + SEPARATOR +
                mPhoneNumber + SEPARATOR + mStartDate + SEPARATOR + mStartTime + SEPARATOR +
                mRoomNumber + SEPARATOR + mShipName;
    }
}
