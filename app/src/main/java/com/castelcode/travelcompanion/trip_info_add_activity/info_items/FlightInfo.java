package com.castelcode.travelcompanion.trip_info_add_activity.info_items;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;

import com.castelcode.protobuf.TripInfoProtos;

import java.io.Serializable;

public class FlightInfo extends Info implements Parcelable, Serializable {

    @SuppressWarnings("unused")
    private final String type = "Flight";
    @SuppressWarnings("WeakerAccess")
    public static final String typeOfItem = "Flight";

    private String mFlightNumber;
    private String mSeatNumber;
    private String mOrigin;
    private String mDestination;
    private String mArrivalTime;

    public FlightInfo(String primaryName, String confirmationNumber, String phoneNumber,
                      String flightNumber, String seatNumber, String origin, String dest,
                      String departureTime, String arrivalTime, String departureDate){
        super(primaryName, confirmationNumber, phoneNumber, departureDate, departureTime);
        mFlightNumber = flightNumber;
        mSeatNumber = seatNumber;
        mOrigin = origin;
        mDestination = dest;
        mArrivalTime = arrivalTime;
    }

    @VisibleForTesting()
    public FlightInfo(Parcel in) {
        mPrimaryName = in.readString();
        mConfirmationNumber = in.readString();
        mPhoneNumber = in.readString();
        mStartDate = in.readString();
        mStartTime = in.readString();
        mFlightNumber = in.readString();
        mSeatNumber = in.readString();
        mOrigin = in.readString();
        mDestination = in.readString();
        mArrivalTime = in.readString();
    }

    public FlightInfo(String shareableString){
        String [] parts = shareableString.split("\\|");
        if(parts.length < 11)
            return;
        mPrimaryName = parts[1];
        mConfirmationNumber = parts[2];
        mPhoneNumber = parts[3];
        mStartDate = parts[4];
        mStartTime = parts[5];
        mFlightNumber = parts[6];
        mSeatNumber = parts[7];
        mOrigin = parts[8];
        mDestination = parts[9];
        mArrivalTime = parts[10];
    }

    public FlightInfo(TripInfoProtos.FlightInformation flightInfo) {
        mPrimaryName = flightInfo.getAirline();
        mConfirmationNumber = flightInfo.getConfirmationNumber();
        mPhoneNumber = flightInfo.getPhoneNumber();
        mStartDate = flightInfo.getDepartureDate();
        mStartTime = flightInfo.getDepartureTime();
        mFlightNumber = flightInfo.getFlightNumber();
        mSeatNumber = flightInfo.getSeatNumber();
        mOrigin = flightInfo.getOrigin();
        mDestination = flightInfo.getDestination();
        mArrivalTime = flightInfo.getArrivalTime();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPrimaryName);
        dest.writeString(mConfirmationNumber);
        dest.writeString(mPhoneNumber);
        dest.writeString(mStartDate);
        dest.writeString(mStartTime);
        dest.writeString(mFlightNumber);
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
            return new FlightInfo(in);
        }

        @Override
        public Info[] newArray(int size) {
            return new Info[size];
        }
    };

    public String getFlightNumber(){
        return mFlightNumber;
    }

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
        return mPrimaryName + " "  + mFlightNumber + " - " + mConfirmationNumber;
    }

    @Override
    public boolean equals(Object o){
        if(o != null && o instanceof FlightInfo){
            FlightInfo other = (FlightInfo)o;
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
                mFlightNumber + SEPARATOR + mSeatNumber + SEPARATOR +
                mOrigin + SEPARATOR + mDestination + SEPARATOR +
                mArrivalTime;
    }
}
