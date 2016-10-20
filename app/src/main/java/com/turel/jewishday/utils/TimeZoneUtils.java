package com.turel.jewishday.utils;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.turel.jewishday.R;

import org.xmlpull.v1.XmlPullParserException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by CHAIMT on 10/31/2014.
 */
public class TimeZoneUtils {
    private static final String TAG = "ZoneList";
    public static final String KEY_ID = "id";
    public static final String KEY_DISPLAYNAME = "name";
    public static final String KEY_GMT = "gmt";
    private static final String KEY_OFFSET = "offset";
    private static final String XMLTAG_TIMEZONE = "timezone";
    private static final int HOURS_1 = 60 * 60000;

    private static void addItem(List<HashMap> myData, String id, String displayName,
                         long date) {
        HashMap map = new HashMap();
        map.put(KEY_ID, id);
        map.put(KEY_DISPLAYNAME, displayName);
        TimeZone tz = TimeZone.getTimeZone(id);
        int offset = tz.getOffset(date);
        int p = Math.abs(offset);
        StringBuilder name = new StringBuilder();
        name.append("GMT");

        if (offset < 0) {
            name.append('-');
        } else {
            name.append('+');
        }

        name.append(p / (HOURS_1));
        name.append(':');

        int min = p / 60000;
        min %= 60;

        if (min < 10) {
            name.append('0');
        }
        name.append(min);

        map.put(KEY_GMT, name.toString());
        map.put(KEY_OFFSET, offset);

        if (id.equals(TimeZone.getDefault().getID())) {
//            mDefault = myData.size();
        }
        myData.add(map);
    }

    public static List<HashMap> getZones(Context context) {
        List<HashMap> myData = new ArrayList<HashMap>();
        long date = Calendar.getInstance().getTimeInMillis();
        try {
            XmlResourceParser xrp = context.getResources().getXml(R.xml.timezones);
            while (xrp.next() != XmlResourceParser.START_TAG)
                continue;
            xrp.next();
            while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                while (xrp.getEventType() != XmlResourceParser.START_TAG) {
                    if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
                        return myData;
                    }
                    xrp.next();
                }
                if (xrp.getName().equals(XMLTAG_TIMEZONE)) {
                    String id = xrp.getAttributeValue(0);
                    String displayName = xrp.nextText();
                    addItem(myData, id, displayName, date);
                }
                while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                    xrp.next();
                }
                xrp.next();
            }
            xrp.close();
        } catch (XmlPullParserException xppe) {
            Log.e(TAG, "Ill-formatted timezones.xml file");
        } catch (java.io.IOException ioe) {
            Log.e(TAG, "Unable to read timezones.xml file");
        }

        return myData;
    }
}
