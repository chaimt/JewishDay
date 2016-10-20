package com.turel.jewishday.utils;

import android.content.Context;
import android.location.Location;
import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;
import android.test.mock.MockContext;

import com.turel.jewishday.R;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Any;

/**
 * Created by Haim.Turkel on 9/16/2015.
 */
public class LocationUtilsTest extends InstrumentationTestCase {

    @Mock
    Location location;

    Context context;


    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        context = getInstrumentation().getContext();
        //Mockito.when(mockBindingResult.hasErrors()).thenReturn(false);
    }

    @Test
    public void testGetLatLng() throws Exception {
        Mockito.when(location.getLatitude()).thenReturn(30.d);
        Mockito.when(location.getLongitude()).thenReturn(34.d);
        //Mockito.when(context.getString(R.string.latitude_longitude)).thenReturn("%1$.8f, %2$.8f");

        String latLng = LocationUtils.getLatLng(context, location);
        Assert.assertNotNull(latLng);
    }
}