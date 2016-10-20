package com.turel.jewishday.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import com.turel.jewishday.utils.Utils;

import java.util.Date;

public class TimePreference extends DialogPreference {
	private java.text.DateFormat userTimeFormat;
	private int lastHour=0;
	private int lastMinute=0;
	private String time;
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	private TimePicker picker=null;



	public int getHour(String time) {
		Date tempDate = Utils.tryParse(userTimeFormat, time, new Date(2000, 0, 0, 0, 0));
		return tempDate.getHours();
	}

	public int getMinute(String time) {
		Date tempDate = Utils.tryParse(userTimeFormat,time, new Date(2000, 0, 0, 0, 0));
		return tempDate.getMinutes();
	}

	public TimePreference(Context ctxt) {
		this(ctxt, null);
	}

	public TimePreference(Context ctxt, AttributeSet attrs) {
		this(ctxt, attrs, 0);
	}

	public TimePreference(Context ctxt, AttributeSet attrs, int defStyle) {
		super(ctxt, attrs, defStyle);

		setPositiveButtonText("Set");
		setNegativeButtonText("Cancel");
		userTimeFormat = DateFormat.getTimeFormat(ctxt);
	}

	@Override
	protected View onCreateDialogView() {
		picker=new TimePicker(getContext());

		return(picker);
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);

		picker.setCurrentHour(lastHour);
		picker.setCurrentMinute(lastMinute);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			lastHour=picker.getCurrentHour();
			lastMinute=picker.getCurrentMinute();

			Date timeDate = new Date(0,0,0,lastHour,lastMinute);
			time = userTimeFormat.format(timeDate);
//			StringBuilder builder = new StringBuilder();
//			if (lastHour<10)
//				builder.append("0");
//			builder.append(lastHour);
//			builder.append(":");
//			if (lastMinute<10)
//				builder.append(lastMinute);
//			time=builder.toString();//String.valueOf(lastHour)+":"+String.valueOf(lastMinute);

			if (callChangeListener(time)) {
				persistString(time);
			}
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return(a.getString(index));
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		time=null;

		if (restoreValue) {
			if (defaultValue==null) {
				time=getPersistedString("00:00");
			}
			else {
				time=getPersistedString(defaultValue.toString());
			}
		}
		else {
			time=defaultValue.toString();
		}

		lastHour=getHour(time);
		lastMinute=getMinute(time);
	}
}