package com.castelcode.travelcompanion.conversions;

public class Conversion {
    protected double mConversionRate;

    public Conversion(double conversionRate){
        mConversionRate = conversionRate;
    }

    public double execute(double originalValue) {
        return originalValue / mConversionRate;
    }
}
