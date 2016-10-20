package com.turel.jewishday.dialog;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class TimeZoneSpinnerPreference extends SpinnerPreference {

	public TimeZoneSpinnerPreference(Context ctxt) {
		super(ctxt);
	}

	public TimeZoneSpinnerPreference(Context ctxt, AttributeSet attrs) {
		this(ctxt, attrs, 0);
	}

	public TimeZoneSpinnerPreference(Context ctxt, AttributeSet attrs, int defStyle) {
		super(ctxt, attrs, defStyle);
	}

	@Override
	protected void setupSpinner() {
		super.setupSpinner();

		// set time zone
		String[] TZ = TimeZone.getAvailableIDs();
		ArrayList<String> TZ1 = new ArrayList<String>();
		int selection = -1;
		Date today = new Date();		
		for (int i = 0; i < TZ.length; i++) {
			TimeZone tz = TimeZone.getTimeZone(TZ[i]);
			String shortName = tz.getDisplayName(tz.inDaylightTime(today), TimeZone.SHORT);
			String longName = tz.getDisplayName(tz.inDaylightTime(today), TimeZone.LONG);
			String timeZoneName = String.format("%s (%s)", longName,shortName);

			if (!(TZ1.contains(timeZoneName))) {
				try {
					TZ1.add(timeZoneName);
					mAdapter.add(timeZoneName);
					if (timeZoneName.equals(TimeZone.getDefault().getDisplayName()))
						selection = i;
				} catch (Exception e) {
					mAdapter.add(e.getMessage());
				}
			}
		}
		spinner.setAdapter(mAdapter);
		if (selection != -1)
			spinner.setSelection(selection);
	}

}
