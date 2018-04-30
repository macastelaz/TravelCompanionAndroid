package com.castelcode.cruisecompanion.conversions_tests;

import com.castelcode.cruisecompanion.conversions.Conversion;
import com.castelcode.cruisecompanion.conversions.distance.*;
import com.castelcode.cruisecompanion.utils.ConversionConstants;

import org.junit.Test;
import static org.junit.Assert.*;

public class DistancesUnitTest {
    @Test
    public void ConversionConstant_Values() throws Exception {
        Double acceptableDelta = 0.0000000001;
        assertEquals(1.60933999997549/0.9999975145, ConversionConstants.KMtoMI,
                acceptableDelta);
        assertEquals(1.852, ConversionConstants.KMtoNM,
                acceptableDelta);
        assertEquals(0.8689760000037314/0.99999972163, ConversionConstants.NMtoMI,
                acceptableDelta);
        assertEquals(0.539957/1.000000364, ConversionConstants.NMtoKM,
                acceptableDelta);
        assertEquals(1.150780000398/1.00000048, ConversionConstants.MItoNM,
                acceptableDelta);
        assertEquals(0.6213709999975146/0.99999969062, ConversionConstants.MItoKM,
                acceptableDelta);
    }

    @Test
    public void Conversion_Execution() throws Exception {
        Double acceptableDelta = 0.00001;
        Conversion KMtoMIConversion = new KMtoMI();
        assertEquals(3.10686, KMtoMIConversion.execute(5.0), acceptableDelta);
        Conversion KMtoNMConversion = new KMtoNM();
        assertEquals(2.69978, KMtoNMConversion.execute(5.0), acceptableDelta);
        Conversion NMtoMIConversion = new NMtoMI();
        assertEquals(5.7539, NMtoMIConversion.execute(5.0), acceptableDelta);
        Conversion NMtoKMConversion = new NMtoKM();
        assertEquals(9.26, NMtoKMConversion.execute(5.0), acceptableDelta);
        Conversion MItoNMConversion = new MItoNM();
        assertEquals(4.34488, MItoNMConversion.execute(5.0), acceptableDelta);
        Conversion MItoKMConversion = new MItoKM();
        assertEquals(8.04672, MItoKMConversion.execute(5.0), acceptableDelta);
    }
}
