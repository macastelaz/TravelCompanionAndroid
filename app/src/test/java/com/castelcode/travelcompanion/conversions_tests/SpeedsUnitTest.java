package com.castelcode.travelcompanion.conversions_tests;

import com.castelcode.travelcompanion.conversions.Conversion;
import com.castelcode.travelcompanion.conversions.speed.*;
import com.castelcode.travelcompanion.utils.ConversionConstants;

import org.junit.Test;
import static org.junit.Assert.*;

public class SpeedsUnitTest {
    @Test
    public void ConversionConstant_Values() throws Exception {
        Double acceptableDelta = 0.0000000001;
        assertEquals(0.539957/1.000000364, ConversionConstants.KNOTStoKPH,
                acceptableDelta);
        assertEquals(0.8689759999984573/0.99999972076, ConversionConstants.KNOTStoMPH,
                acceptableDelta);
        assertEquals(1.1507800005543/1.000000481, ConversionConstants.MPHtoKNOTS,
                acceptableDelta);
        assertEquals(0.6213709999975642/0.99999969062, ConversionConstants.MPHtoKPH,
                acceptableDelta);
        assertEquals(1.60933999997536/0.9999975145, ConversionConstants.KPHtoMPH,
                acceptableDelta);
        assertEquals(1.852, ConversionConstants.KPHtoKNOTS,
                acceptableDelta);
    }

    @Test
    public void Conversion_Execution() throws Exception {
        Double acceptableDelta = 0.00001;
        Conversion KNOTStoKPHConversion = new KNOTStoKPH();
        assertEquals(9.26, KNOTStoKPHConversion.execute(5.0), acceptableDelta);
        Conversion KNOTStoMPHConversion = new KNOTStoMPH();
        assertEquals(5.7539, KNOTStoMPHConversion.execute(5.0), acceptableDelta);
        Conversion MPHtoKNOTSConversion = new MPHtoKNOTS();
        assertEquals(4.34488, MPHtoKNOTSConversion.execute(5.0), acceptableDelta);
        Conversion MPHtoKPHConversion = new MPHtoKPH();
        assertEquals(8.04672, MPHtoKPHConversion.execute(5.0), acceptableDelta);
        Conversion KPHtoMPHConversion = new KPHtoMPH();
        assertEquals(3.10686, KPHtoMPHConversion.execute(5.0), acceptableDelta);
        Conversion KPHtoKNOTSConversion = new KPHtoKNOTS();
        assertEquals(2.69978, KPHtoKNOTSConversion.execute(5.0), acceptableDelta);
    }
}
