package com.castelcode.travelcompanion.conversions.temperatures;

import com.castelcode.travelcompanion.conversions.Conversion;
import com.castelcode.travelcompanion.utils.ConversionConstants;

public class FtoC extends Conversion {

    public FtoC(){
        super(ConversionConstants.FtoC);
    }

    @Override
    public double execute(double originalValue){
        return (originalValue - ConversionConstants.CFConstant) * mConversionRate;
    }
}
