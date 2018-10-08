package com.castelcode.cruisecompanion.trip_checklists;

import android.os.Parcel;
import android.os.Parcelable;

import com.castelcode.cruisecompanion.TripChecklistProtos;

import java.io.Serializable;
import java.util.ArrayList;

public class Checklist implements Parcelable, Serializable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Checklist createFromParcel(Parcel in) {
            return new Checklist(in);
        }
        public Checklist[] newArray(int size) {
            return new Checklist[size];
        }
    };

    private String checklistName;
    private ArrayList<ChecklistItem> items = new ArrayList<>();
    private boolean isRateable;

    @SuppressWarnings({"unchecked", "WeakerAccess"})
    public Checklist(Parcel in) {
        checklistName = in.readString();
        items = new ArrayList<>();
        in.readTypedList(items, ChecklistItem.CREATOR);
        this.isRateable = in.readByte() != 0; // isRateable == true if byte != 0;
    }

    public Checklist(String name, boolean isRateable) {
        this.checklistName = name;
        this.isRateable = isRateable;
    }

    public Checklist(TripChecklistProtos.Checklist checklistIn) {
        this.checklistName = checklistIn.getName();
        this.isRateable = checklistIn.getRateable();
        for (TripChecklistProtos.ChecklistItem item : checklistIn.getItemsList()) {
            this.addChecklistItem(new ChecklistItem(item));
        }
    }

    public String getChecklistName() {
        return checklistName;
    }

    public boolean getIsRateable() {
        return isRateable;
    }

    public boolean addChecklistItem(ChecklistItem item) {
        if (!items.contains(item)) {
            items.add(item);
            return true;
        }
        return false;
    }

    public ArrayList<ChecklistItem> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Checklist && this.checklistName.equals(((Checklist) o).checklistName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(checklistName);
        parcel.writeTypedList(items);
        parcel.writeByte((byte) (isRateable ? 1 : 0));
    }
}
