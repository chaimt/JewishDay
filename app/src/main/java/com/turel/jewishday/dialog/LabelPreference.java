package com.turel.jewishday.dialog;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class LabelPreference extends DialogPreference {
	private String label;
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public LabelPreference(Context ctxt) {
		this(ctxt, null);
	}

	public LabelPreference(Context ctxt, AttributeSet attrs) {
		this(ctxt, attrs, 0);
	}

	public LabelPreference(Context ctxt, AttributeSet attrs, int defStyle) {
		super(ctxt, attrs, defStyle);

		setPositiveButtonText("Set");
		setNegativeButtonText("Cancel");
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		
		super.onDialogClosed(positiveResult);

		label = (String) getTitle();
		if (positiveResult) {
			if (callChangeListener(label)) {
				persistString(label);
			}
		}
	}


	

}
