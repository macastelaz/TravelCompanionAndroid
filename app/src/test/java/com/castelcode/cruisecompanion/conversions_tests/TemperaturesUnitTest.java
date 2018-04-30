package com.castelcode.cruisecompanion.conversions_tests;

import com.castelcode.cruisecompanion.conversions.Conversion;
import com.castelcode.cruisecompanion.conversions.temperatures.*;
import com.castelcode.cruisecompanion.utils.ConversionConstants;

import org.junit.Test;
import static org.junit.Assert.*;

public class TemperaturesUnitTest {
    @Test
    public void ConversionConstant_Values() throws Exception {
        Double acceptableDelta = 0.0001;
        assertEquals(1.8, ConversionConstants.CtoF,
                acceptableDelta);
        assertEquals(0.5556, ConversionConstants.FtoC,
                acceptableDelta);
    }

    @Test
    public void Conversion_Execution() throws Exception {
        Double acceptableDelta = 0.01;
        Conversion CtoFConversion = new CtoF();
        assertEquals(41, CtoFConversion.execute(5.0), acceptableDelta);
        Conversion FtoCConversion = new FtoC();
        assertEquals(-15, FtoCConversion.execute(5.0), acceptableDelta);
    }
}
