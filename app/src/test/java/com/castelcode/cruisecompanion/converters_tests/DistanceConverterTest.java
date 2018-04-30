package com.castelcode.cruisecompanion.converters_tests;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.test.mock.MockResources;
import android.util.Log;

import com.castelcode.cruisecompanion.R;
import com.castelcode.cruisecompanion.converters.DistanceConverter;

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
public class DistanceConverterTest {
    @Mock
    private
    Context mMockContext;

    private String[] mockedSupportedDistances = new String[] {"MI", "KM", "NM"};

    @Test
    public void Convert() throws Exception {
        Resources res = new MockResources() {
            @Override
            @NonNull
            public String[] getStringArray(int id) {
                if (id == R.array.supported_distances) {
                    return mockedSupportedDistances;
                }
                return new String[]{};
            }
            @Override
            @NonNull
            public String getString(int id) {
                if (id == R.string.MI) {
                    return "MI";
                } else if (id == R.string.NM) {
                    return "NM";
                } else if (id == R.string.KM) {
                    return "KM";
                } else {
                    return "";
                }
            }
        };
        PowerMockito.mockStatic(Log.class);
        when(mMockContext.getResources())
                .thenReturn(res);
        double acceptableDelta = 0.01;
        DistanceConverter converter = new DistanceConverter(mMockContext);
        converter.setOriginalValue(5.0);
        assertEquals(8.05 ,converter.convert("MI", "KM"),
                acceptableDelta);
        assertEquals(3.11 ,converter.convert("KM", "MI"),
                acceptableDelta);
        assertEquals(2.69978 ,converter.convert("KM", "NM"),
                acceptableDelta);
        assertEquals(9.26 ,converter.convert("NM", "KM"),
                acceptableDelta);
        assertEquals(4.34488 ,converter.convert("MI", "NM"),
                acceptableDelta);
        assertEquals(5.7539 ,converter.convert("NM", "MI"),
                acceptableDelta);
    }
}
