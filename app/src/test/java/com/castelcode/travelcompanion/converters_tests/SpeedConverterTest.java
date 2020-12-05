package com.castelcode.travelcompanion.converters_tests;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.test.mock.MockResources;
import android.util.Log;

import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.converters.SpeedConverter;

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
public class SpeedConverterTest {
    @Mock
    private
    Context mMockContext;

    private String[] mockedSupportedSpeeds = new String[] {"MPH", "KPH", "KNOTS"};

    @Test
    public void Convert() throws Exception {
        Resources res = new MockResources() {
            @Override
            @NonNull
            public String[] getStringArray(int id) {
                if (id == R.array.supported_speeds) {
                    return mockedSupportedSpeeds;
                }
                return new String[]{};
            }

            @Override
            @NonNull
            public String getString(int id) {
                if (id == R.string.KPH) {
                    return "KPH";
                } else if (id == R.string.MPH) {
                    return "MPH";
                } else if (id == R.string.KNOTS) {
                    return "KNOTS";
                } else {
                    return "";
                }
            }
        };
        PowerMockito.mockStatic(Log.class);
        when(mMockContext.getResources())
                .thenReturn(res);
        double acceptableDelta = 0.01;
        SpeedConverter converter = new SpeedConverter(mMockContext);
        converter.setOriginalValue(5.0);
        assertEquals(8.04672, converter.convert("MPH", "KPH"),
                acceptableDelta);
        assertEquals(3.10686, converter.convert("KPH", "MPH"),
                acceptableDelta);
        assertEquals(2.69978, converter.convert("KPH", "KNOTS"),
                acceptableDelta);
        assertEquals(9.26, converter.convert("KNOTS", "KPH"),
                acceptableDelta);
        assertEquals(4.34488, converter.convert("MPH", "KNOTS"),
                acceptableDelta);
        assertEquals(5.7539, converter.convert("KNOTS", "MPH"),
                acceptableDelta);
    }
}
