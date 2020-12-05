package com.castelcode.travelcompanion.conversions.temperatures;

import com.castelcode.travelcompanion.conversions.Conversion;
import com.castelcode.travelcompanion.utils.ConversionConstants;

public class CtoF extends Conversion {

    public CtoF(){
        super(ConversionConstants.CtoF);
    }

    @Override
    public double execute(double originalValue){
        return (originalValue * mConversionRate) + ConversionConstants.CFConstant;
    }
}
