package com.castelcode.cruisecompanion.conversions.temperatures;

import com.castelcode.cruisecompanion.conversions.Conversion;
import com.castelcode.cruisecompanion.utils.ConversionConstants;

public class FtoC extends Conversion {

    public FtoC(){
        super(ConversionConstants.FtoC);
    }

    @Override
    public double execute(double originalValue){
        return (originalValue - ConversionConstants.CFConstant) * mConversionRate;
    }
}
