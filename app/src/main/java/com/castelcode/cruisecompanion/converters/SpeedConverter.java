package com.castelcode.cruisecompanion.converters;

import android.content.Context;

import com.castelcode.cruisecompanion.conversions.Conversion;
import com.castelcode.cruisecompanion.conversions.speed.KNOTStoKPH;
import com.castelcode.cruisecompanion.conversions.speed.KNOTStoMPH;
import com.castelcode.cruisecompanion.conversions.speed.KPHtoKNOTS;
import com.castelcode.cruisecompanion.conversions.speed.KPHtoMPH;
import com.castelcode.cruisecompanion.conversions.speed.MPHtoKNOTS;
import com.castelcode.cruisecompanion.conversions.speed.MPHtoKPH;
import com.castelcode.cruisecompanion.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpeedConverter extends Converter {
    private final String MPH = mContext.getResources().getString(R.string.MPH);
    private final String KNOTS = mContext.getResources().getString(R.string.KNOTS);
    private final String KPH = mContext.getResources().getString(R.string.KPH);

    public SpeedConverter(Context context){
        super(context);
        String[] myResArray = mContext.getResources().getStringArray(
                R.array.supported_speeds);
        List<String> myResArrayList = Arrays.asList(myResArray);
        supportedUnits = new ArrayList<>(myResArrayList);
        setupPossibleConversions();
    }

    private void setupPossibleConversions(){
        ArrayList<String> supportedSpeedsCopy = new ArrayList<>();
        supportedSpeedsCopy.addAll(supportedUnits);
        String removedItem;
        for (String originalUnit: supportedUnits) {
            int indexOfRemovedItem = supportedUnits.indexOf(originalUnit);
            removedItem = supportedSpeedsCopy.remove(indexOfRemovedItem);
            for(String destinationUnit: supportedSpeedsCopy){
                Conversion conversionItem = createConversion(originalUnit, destinationUnit);
                possibleConversions.put(originalUnit+destinationUnit, conversionItem);
            }
            supportedSpeedsCopy.add(indexOfRemovedItem, removedItem);
        }
    }

    private Conversion createConversion(String originalUnit, String destinationUniit){
        if(originalUnit.equals(MPH))
        {
            if(destinationUniit.equals(KNOTS)){
                return new MPHtoKNOTS();
            }
            else if(destinationUniit.equals(KPH)){
                return new MPHtoKPH();
            }
        }
        else if(originalUnit.equals(KNOTS)){
            if(destinationUniit.equals(MPH)){
                return new KNOTStoMPH();
            }
            else if(destinationUniit.equals(KPH)){
                return new KNOTStoKPH();
            }
        }
        else if(originalUnit.equals(KPH)){
            if(destinationUniit.equals(MPH)){
                return new KPHtoMPH();
            }
            else if(destinationUniit.equals(KNOTS)){
                return new KPHtoKNOTS();
            }
        }
        return null;
    }
}
