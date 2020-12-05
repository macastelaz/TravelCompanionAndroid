package com.castelcode.travelcompanion.converters;

import android.content.Context;

import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.conversions.Conversion;
import com.castelcode.travelcompanion.conversions.temperatures.CtoF;
import com.castelcode.travelcompanion.conversions.temperatures.FtoC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TemperatureConverter extends Converter {
    private final String F = mContext.getResources().getString(R.string.F);
    private final String C = mContext.getResources().getString(R.string.C);

    public TemperatureConverter(Context context){
        super(context);
        String[] myResArray = mContext.getResources().getStringArray(
                R.array.supported_temperatures);
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
        if(originalUnit.equals(C))
        {
            if(destinationUniit.equals(F)){
                return new CtoF();
            }
        }
        else if(originalUnit.equals(F)){
            if(destinationUniit.equals(C)){
                return new FtoC();
            }
        }
        return null;
    }
}
