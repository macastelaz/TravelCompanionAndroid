package com.castelcode.travelcompanion.converters;

import android.content.Context;

import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.conversions.Conversion;
import com.castelcode.travelcompanion.conversions.distance.KMtoMI;
import com.castelcode.travelcompanion.conversions.distance.KMtoNM;
import com.castelcode.travelcompanion.conversions.distance.MItoKM;
import com.castelcode.travelcompanion.conversions.distance.MItoNM;
import com.castelcode.travelcompanion.conversions.distance.NMtoKM;
import com.castelcode.travelcompanion.conversions.distance.NMtoMI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DistanceConverter extends Converter {

    private final String MI = mContext.getResources().getString(R.string.MI);
    private final String KM = mContext.getResources().getString(R.string.KM);
    private final String NM = mContext.getResources().getString(R.string.NM);

    public DistanceConverter(Context context){
        super(context);
        String[] myResArray = mContext.getResources().getStringArray(
                R.array.supported_distances);
        List<String> myResArrayList = Arrays.asList(myResArray);
        supportedUnits = new ArrayList<>(myResArrayList);
            setupPossibleConversions();
    }

    private void setupPossibleConversions(){
        ArrayList<String> supportedDistancesCopy = new ArrayList<>();
        supportedDistancesCopy.addAll(supportedUnits);
        String removedItem;
        for (String originalUnit: supportedUnits) {
            int indexOfRemovedItem = supportedUnits.indexOf(originalUnit);
            removedItem = supportedDistancesCopy.remove(indexOfRemovedItem);
            for(String destinationUnit: supportedDistancesCopy){
                Conversion conversionItem = createConversion(originalUnit, destinationUnit);
                possibleConversions.put(originalUnit+destinationUnit, conversionItem);
            }
            supportedDistancesCopy.add(indexOfRemovedItem, removedItem);
        }
    }

    private Conversion createConversion(String originalUnit, String destinationUniit){
        if(originalUnit.equals(MI))
        {
            if(destinationUniit.equals(KM)){
                return new MItoKM();
            }
            else if(destinationUniit.equals(NM)){
                return new MItoNM();
            }
        }
        else if(originalUnit.equals(KM)){
            if(destinationUniit.equals(MI)){
                return new KMtoMI();
            }
            else if(destinationUniit.equals(NM)){
                return new KMtoNM();
            }
        }
        else if(originalUnit.equals(NM)){
            if(destinationUniit.equals(KM)){
                return new NMtoKM();
            }
            else if(destinationUniit.equals(MI)){
                return new NMtoMI();
            }
        }
        return null;
    }

}
