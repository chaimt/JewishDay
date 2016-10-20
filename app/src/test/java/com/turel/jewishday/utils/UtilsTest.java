package com.turel.jewishday.utils;


import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class UtilsTest {

    @Test
    public void getTime(){
        int hourOfDay = 4;
        int minute = 3;
        Date time = Utils.getTime(hourOfDay, minute);
        Assert.assertNotNull(time);
        Assert.assertEquals(-62146389420000L, time.getTime());
    }

    @Test
    public void truncate(){
        Date time = new Date();
        Date truncate = Utils.truncate(time);

        Assert.assertNotNull(truncate);
        Assert.assertEquals(0, truncate.getHours());
        Assert.assertEquals(0, truncate.getMinutes());
    }
}