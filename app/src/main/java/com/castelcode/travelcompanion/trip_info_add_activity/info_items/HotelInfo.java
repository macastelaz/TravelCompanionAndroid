package com.castelcode.travelcompanion.trip_info_add_activity.info_items;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;

import com.castelcode.protobuf.TripInfoProtos;

import java.io.Serializable;

public class HotelInfo extends Info implements Parcelable, Serializable {

    @SuppressWarnings("unused")
    private final String type = "Hotel";
    @SuppressWarnings("WeakerAccess")
    public static final String typeOfItem = "Hotel";

    private String mAddress;
    private String mCity;
    private String mStateProvince;
    private String mCheckOutDate;
    private String mCheckOutTime;

    public HotelInfo(String primaryName, String confirmationNumber, String phoneNumber,
                      String address, String city, String stateProvince, String checkInDate,
                     String checkInTime, String checkOutDate, String checkOutTime){
        super(primaryName, confirmationNumber, phoneNumber, checkInDate, checkInTime);
        mAddress = address;
        mCity = city;
        mStateProvince = stateProvince;
        mCheckOutDate = checkOutDate;
        mCheckOutTime = checkOutTime;
    }

    @VisibleForTesting()
    public HotelInfo(Parcel in) {
        mPrimaryName = in.readString();
        mConfirmationNumber = in.readString();
        mPhoneNumber = in.readString();
        mStartDate = in.readString();
        mStartTime = in.readString();
        mAddress = in.readString();
        mCity = in.readString();
        mStateProvince = in.readString();
        mCheckOutDate = in.readString();
        mCheckOutTime = in.readString();
    }

    public HotelInfo(String shareableString){
        String [] parts = shareableString.split("\\|");
        if(parts.length < 11)
            return;
        mPrimaryName = parts[1];
        mConfirmationNumber = parts[2];
        mPhoneNumber = parts[3];
        mStartDate = parts[4];
        mStartTime = parts[5];
        mAddress = parts[6];
        mCity = parts[7];
        mStateProvince = parts[8];
        mCheckOutDate = parts[9];
        mCheckOutTime = parts[10];
    }

    public HotelInfo(TripInfoProtos.HotelInformation hotelInfo) {
        mPrimaryName = hotelInfo.getName();
        mConfirmationNumber = hotelInfo.getConfrimationNumber();
        mPhoneNumber = hotelInfo.getPhoneNumber();
        mStartDate = hotelInfo.getCheckInDate();
        mStartTime = hotelInfo.getCheckInTime();
        mAddress = hotelInfo.getAddress();
        mCity = hotelInfo.getCity();
        mStateProvince = hotelInfo.getState();
        mCheckOutDate = hotelInfo.getCheckOutDate();
        mCheckOutTime = hotelInfo.getCheckOutTime();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPrimaryName);
        dest.writeString(mConfirmationNumber);
        dest.writeString(mPhoneNumber);
        dest.writeString(mStartDate);
        dest.writeString(mStartTime);
        dest.writeString(mAddress);
        dest.writeString(mCity);
        dest.writeString(mStateProvince);
        dest.writeString(mCheckOutDate);
        dest.writeString(mCheckOutTime);

    }
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Info> CREATOR = new Creator<Info>() {
        @Override
        public Info createFromParcel(Parcel in) {
            return new HotelInfo(in);
        }

        @Override
        public Info[] newArray(int size) {
            return new Info[size];
        }
    };

    public String getAddress(){
        return mAddress;
    }

    public String getCity(){
        return mCity;
    }

    public String getStateProvince(){
        return mStateProvince;
    }

    public String getCheckOutDate(){
        return mCheckOutDate;
    }

    public String getCheckOutTime(){
        return mCheckOutTime;
    }


    @Override
    public String toString(){
        return mPrimaryName + " - " + mConfirmationNumber;
    }

    @Override
    public String toShareableString() {
        String SEPARATOR = "|";
        return typeOfItem + SEPARATOR + mPrimaryName + SEPARATOR + mConfirmationNumber + SEPARATOR +
                mPhoneNumber + SEPARATOR + mStartDate + SEPARATOR + mStartTime + SEPARATOR +
                mAddress + SEPARATOR + mCity + SEPARATOR + mStateProvince + SEPARATOR +
                mCheckOutDate + SEPARATOR + mCheckOutTime;
    }

}
