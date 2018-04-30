package com.castelcode.cruisecompanion.conversions.temperatures;

import com.castelcode.cruisecompanion.conversions.Conversion;
import com.castelcode.cruisecompanion.utils.ConversionConstants;

public class CtoF extends Conversion {

    public CtoF(){
        super(ConversionConstants.CtoF);
    }

    @Override
    public double execute(double originalValue){
        return (originalValue * mConversionRate) + ConversionConstants.CFConstant;
    }
}
