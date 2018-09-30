package com.castelcode.cruisecompanion.trip_checklists;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ChecklistItem implements Parcelable, Serializable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ChecklistItem createFromParcel(Parcel in) {
            return new ChecklistItem(in);
        }
        public ChecklistItem[] newArray(int size) {
            return new ChecklistItem[size];
        }
    };

    private String itemTitle;
    private boolean checkedState;
    private float rating;
    private int originalPosition;

    @SuppressWarnings("WeakerAccess")
    public ChecklistItem(Parcel in) {
        this.itemTitle = in.readString();
        this.checkedState = in.readByte() != 0; // checkedState == true if byte != 0;
        this.rating = in.readFloat();
        this.originalPosition = in.readInt();
    }

    public ChecklistItem(String itemTitle, int position) {
        this.itemTitle = itemTitle;
        checkedState = false;
        rating = 0;
        originalPosition = position;
    }

    public void toggleCheckedState() {
        checkedState = !checkedState;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public boolean getCheckedState() {
        return checkedState;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getRating() {
        return rating;
    }

    public int getOriginalPosition() {
        return originalPosition;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ChecklistItem && this.itemTitle.equals(((ChecklistItem) o).itemTitle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(itemTitle);
        parcel.writeByte((byte) (checkedState ? 1 : 0));
        parcel.writeFloat(rating);
        parcel.writeInt(originalPosition);
    }
}
