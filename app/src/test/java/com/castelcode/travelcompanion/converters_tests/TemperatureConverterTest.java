package com.castelcode.travelcompanion.converters_tests;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.test.mock.MockResources;
import android.util.Log;

import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.converters.TemperatureConverter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class TemperatureConverterTest {
    @Mock
    private
    Context mMockContext;

    private String[] mockedSupportedTemperatures = new String[] {"C", "F"};

    @Test
    public void Convert() throws Exception {
        Resources res = new MockResources() {
            @Override
            @NonNull
            public String[] getStringArray(int id) {
                if (id == R.array.supported_temperatures) {
                    return mockedSupportedTemperatures;
                }
                return new String[]{};
            }

            @Override
            @NonNull
            public String getString(int id) {
                if (id == R.string.F) {
                    return "F";
                } else if (id == R.string.C) {
                    return "C";
                } else {
                    return "";
                }
            }
        };
        PowerMockito.mockStatic(Log.class);
        when(mMockContext.getResources())
                .thenReturn(res);
        double acceptableDelta = 0.01;
        TemperatureConverter converter = new TemperatureConverter(mMockContext);
        converter.setOriginalValue(5.0);
        assertEquals(-15, converter.convert("F", "C"),
                acceptableDelta);
        assertEquals(41, converter.convert("C", "F"),
                acceptableDelta);
    }
}
