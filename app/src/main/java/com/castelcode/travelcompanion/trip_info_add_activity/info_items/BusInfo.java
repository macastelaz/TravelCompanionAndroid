package com.castelcode.travelcompanion.trip_info_add_activity.info_items;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;

import com.castelcode.protobuf.TripInfoProtos;

import java.io.Serializable;

public class BusInfo extends Info implements Parcelable, Serializable {

    @SuppressWarnings("unused")
    private final String type = "Bus";
    @SuppressWarnings("WeakerAccess")
    public static final String typeOfItem = "Bus";

    private String mSeatNumber;
    private String mOrigin;
    private String mDestination;
    private String mArrivalTime;

    public BusInfo(String primaryName, String confirmationNumber, String phoneNumber,
                   String seatNumber, String origin, String dest,
                   String departureTime, String arrivalTime, String departureDate){
        super(primaryName, confirmationNumber, phoneNumber, departureDate, departureTime);
        mSeatNumber = seatNumber;
        mOrigin = origin;
        mDestination = dest;
        mArrivalTime = arrivalTime;
    }

    @VisibleForTesting()
    public BusInfo(Parcel in) {
        mPrimaryName = in.readString();
        mConfirmationNumber = in.readString();
        mPhoneNumber = in.readString();
        mStartDate = in.readString();
        mStartTime = in.readString();
        mSeatNumber = in.readString();
        mOrigin = in.readString();
        mDestination = in.readString();
        mArrivalTime = in.readString();
    }

    public BusInfo(String shareableString){
        String [] parts = shareableString.split("\\|");
        if(parts.length < 10)
            return;
        mPrimaryName = parts[1];
        mConfirmationNumber = parts[2];
        mPhoneNumber = parts[3];
        mStartDate = parts[4];
        mStartTime = parts[5];
        mSeatNumber = parts[6];
        mOrigin = parts[7];
        mDestination = parts[8];
        mArrivalTime = parts[9];
    }

    public BusInfo(TripInfoProtos.BusInformation busInfo) {
        mPrimaryName = busInfo.getBusLine();
        mConfirmationNumber = busInfo.getConfirmationNumber();
        mPhoneNumber = busInfo.getPhoneNumber();
        mStartDate = busInfo.getDepartureDate();
        mStartTime = busInfo.getDepartureTime();
        mSeatNumber = busInfo.getSeatNumber();
        mOrigin = busInfo.getOrigin();
        mDestination = busInfo.getDestination();
        mArrivalTime = busInfo.getArrivalTime();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPrimaryName);
        dest.writeString(mConfirmationNumber);
        dest.writeString(mPhoneNumber);
        dest.writeString(mStartDate);
        dest.writeString(mStartTime);
        dest.writeString(mSeatNumber);
        dest.writeString(mOrigin);
        dest.writeString(mDestination);
        dest.writeString(mArrivalTime);
    }
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Info> CREATOR = new Creator<Info>() {
        @Override
        public Info createFromParcel(Parcel in) {
            return new BusInfo(in);
        }

        @Override
        public Info[] newArray(int size) {
            return new Info[size];
        }
    };

    public String getSeatNumber(){
        return mSeatNumber;
    }

    public String getOrigin(){
        return mOrigin;
    }

    public String getDestination(){
        return mDestination;
    }

    public String getArrivalTime() { return mArrivalTime; }

    @Override
    public String toString(){
        return mPrimaryName + " - " + mConfirmationNumber;
    }

    @Override
    public boolean equals(Object o){
        if(o != null && o instanceof BusInfo){
            BusInfo other = (BusInfo)o;
            return this.mPrimaryName.equals(other.getPrimaryName()) &&
                    this.mConfirmationNumber.equals(other.getConfirmationNumber()) &&
                    this.mOrigin.equals(other.getOrigin()) &&
                    this.mDestination.equals(other.getDestination());
        }
        return false;
    }

    @Override
    public String toShareableString() {
        String SEPARATOR = "|";
        return typeOfItem + SEPARATOR + mPrimaryName + SEPARATOR + mConfirmationNumber + SEPARATOR +
                mPhoneNumber + SEPARATOR + mStartDate + SEPARATOR + mStartTime + SEPARATOR +
                mSeatNumber + SEPARATOR + mOrigin + SEPARATOR + mDestination + SEPARATOR +
                mArrivalTime;
    }
}
