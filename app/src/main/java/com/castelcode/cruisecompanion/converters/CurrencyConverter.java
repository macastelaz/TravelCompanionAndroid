package com.castelcode.cruisecompanion.converters;

import android.content.Context;
import android.util.Log;

import com.castelcode.cruisecompanion.utils.ConversionConstants;

public class CurrencyConverter extends Converter {
    private static final String TAG = "CURRENCY_CONVERTER";

    public CurrencyConverter(Context context) {
        super(context);
    }

    @Override
    public ResultWrapper convert(String originalUnit, String desiredUnit) {
        double euroValue = -1;
        switch (originalUnit) {
            case "CAD":
                euroValue = mOriginalValue / ConversionConstants.CAD;
                break;
            case "USD":
                euroValue = mOriginalValue / ConversionConstants.USD;
                break;
            case "MXN":
                euroValue = mOriginalValue / ConversionConstants.MXN;
                break;
            case "GBP":
                euroValue = mOriginalValue / ConversionConstants.GBP;
                break;
            case "EUR":
                euroValue = mOriginalValue / ConversionConstants.EUR;
                break;
            default:
                Log.i(TAG, "ORIGIN CURRENCY NOT SUPPORTED");
        }
        if (euroValue == -1) {
            return new ResultWrapper(euroValue);
        }
        double destinationValue;
        switch (desiredUnit) {
            case "CAD":
                destinationValue = euroValue * ConversionConstants.CAD;
                break;
            case "USD":
                destinationValue = euroValue * ConversionConstants.USD;
                break;
            case "MXN":
                destinationValue = euroValue * ConversionConstants.MXN;
                break;
            case "GBP":
                destinationValue = euroValue * ConversionConstants.GBP;
                break;
            case "EUR":
                destinationValue = euroValue * ConversionConstants.EUR;
                break;
            default:
                Log.i(TAG, "DESTINATION CURRENCY NOT SUPPORTED");
                destinationValue = -1;
        }
        return new ResultWrapper(destinationValue);
    }
}
