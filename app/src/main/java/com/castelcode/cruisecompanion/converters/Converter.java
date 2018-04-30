package com.castelcode.cruisecompanion.converters;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.castelcode.cruisecompanion.conversions.Conversion;

import java.util.ArrayList;

public abstract class Converter {
    protected Context mContext;
    protected double mOriginalValue;

    //assigned and updated in children classes so suppress
    @SuppressWarnings("WeakerAccess")
    protected ArrayList<String> supportedUnits;
    //assigned and updated in children classes so suppress
    @SuppressWarnings("WeakerAccess")
    protected ArrayMap<String, Conversion> possibleConversions = new ArrayMap<>();

    //assigned and updated in children classes so suppress
    @SuppressWarnings("WeakerAccess")
    protected Converter(Context context){
        mContext = context;
    }

    public void setOriginalValue(double originalValue){
     mOriginalValue = originalValue;
    }

    public double convert(String originalUnit, String desiredUnit){
        if(!supportedUnits.contains(originalUnit) ||
                !supportedUnits.contains(desiredUnit)){
            return -1;
        }
        if(mOriginalValue < 0){
            return -1;
        }
        try{
            return possibleConversions.get(originalUnit+desiredUnit).execute(mOriginalValue);
        }
        catch (Exception ex){
            return -1;
        }
    }
}
