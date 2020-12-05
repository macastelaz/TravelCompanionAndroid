package com.castelcode.travelcompanion.converters_tests;

import android.content.Context;
import android.util.Log;

import com.castelcode.travelcompanion.converters.CurrencyConverter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class CurrencyConverterTest {
    @Mock
    Context mMockContext;

    @Test
    public void Convert() throws Exception {
        PowerMockito.mockStatic(Log.class);
        double acceptableDelta = 0.01;
        CurrencyConverter converter = new CurrencyConverter(mMockContext);
        converter.setOriginalValue(5.0);
        assertEquals(6.30 ,converter.convert("USD", "CAD"),
                acceptableDelta);
        assertEquals(3.97 ,converter.convert("CAD", "USD"),
                acceptableDelta);
        assertEquals(3.53 ,converter.convert("USD", "GBP"),
                acceptableDelta);
        assertEquals(7.09 ,converter.convert("GBP", "USD"),
                acceptableDelta);
        assertEquals(91.27 ,converter.convert("USD", "MXN"),
                acceptableDelta);
        assertEquals(0.27 ,converter.convert("MXN", "USD"),
                acceptableDelta);
        assertEquals(4.04 ,converter.convert("USD", "EUR"),
                acceptableDelta);
        assertEquals(6.18 ,converter.convert("EUR", "USD"),
                acceptableDelta);
        assertEquals(4.04 ,converter.convert("USD", "EUR"),
                acceptableDelta);
        assertEquals(6.18 ,converter.convert("EUR", "USD"),
                acceptableDelta);
        assertEquals(0.19 ,converter.convert("MXN", "GBP"),
                acceptableDelta);
        assertEquals(7.79 ,converter.convert("EUR", "CAD"),
                acceptableDelta);
        assertEquals(-1.0 ,converter.convert("RUP", "EUR"),
                acceptableDelta);
        assertEquals(-1.0 ,converter.convert("EUR", "ITL"),
                acceptableDelta);
    }
}
