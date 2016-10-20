package com.turel.jewishday.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SpinnerPreference extends DialogPreference {
	protected Spinner spinner = null;
	protected String prompt;
	protected int mPos;
	protected String mSelection;
	protected ArrayAdapter<CharSequence> mAdapter;
	protected String selection = null;

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public SpinnerPreference(Context ctxt) {
		this(ctxt, null);
	}

	public SpinnerPreference(Context ctxt, AttributeSet attrs) {
		this(ctxt, attrs, 0);
	}

	public SpinnerPreference(Context ctxt, AttributeSet attrs, int defStyle) {
		super(ctxt, attrs, defStyle);

		setPositiveButtonText("Set");
		setNegativeButtonText("Cancel");
	}

	protected void setupSpinner() {
		spinner = new Spinner(getContext());
		mAdapter = new ArrayAdapter <CharSequence> (getContext(), android.R.layout.simple_spinner_item );
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			 
		// this.mAdapter = ArrayAdapter.createFromResource(this,
		// R.array.Planets,
		// android.R.layout.simple_spinner_dropdown_item);
		OnItemSelectedListener spinnerListener = new myOnItemSelectedListener(this.mAdapter);
		spinner.setOnItemSelectedListener(spinnerListener);
	}

	@Override
	protected View onCreateDialogView() {
		setupSpinner();

		return (spinner);
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);

		spinner.setPrompt(prompt);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {

		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			if (callChangeListener(mSelection)) {
				persistString(mSelection);
			}
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return (a.getString(index));
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		if (restoreValue) {
			if (defaultValue == null) {
				selection = getPersistedString("");
			} else {
				selection = getPersistedString(defaultValue.toString());
			}
		} else {
			selection = defaultValue.toString();
		}
	}

	/**
	 * A callback listener that implements the
	 * {@link OnItemSelectedListener} interface For
	 * views based on adapters, this interface defines the methods available
	 * when the user selects an item from the View.
	 *
	 */
	public class myOnItemSelectedListener implements OnItemSelectedListener {

		/*
		 * provide local instances of the mLocalAdapter and the mLocalContext
		 */

		ArrayAdapter<CharSequence> mLocalAdapter;

		// Activity mLocalContext;

		/**
		 * Constructor
		 *
		 * @param c
		 *            - The activity that displays the Spinner.
		 * @param ad
		 *            - The Adapter view that controls the Spinner. Instantiate
		 *            a new listener object.
		 */
		public myOnItemSelectedListener(ArrayAdapter<CharSequence> ad) {

			// this.mLocalContext = c;
			this.mLocalAdapter = ad;

		}

		/**
		 * When the user selects an item in the spinner, this method is invoked
		 * by the callback chain. Android calls the item selected listener for
		 * the spinner, which invokes the onItemSelected method.
		 *
		 * @see OnItemSelectedListener#onItemSelected(AdapterView,
		 *      View, int, long)
		 * @param parent
		 *            - the AdapterView for this listener
		 * @param v
		 *            - the View for this listener
		 * @param pos
		 *            - the 0-based position of the selection in the
		 *            mLocalAdapter
		 * @param row
		 *            - the 0-based row number of the selection in the View
		 */
		public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {

			SpinnerPreference.this.mPos = pos;
			SpinnerPreference.this.mSelection = parent.getItemAtPosition(pos).toString();
			/*
			 * Set the value of the text field in the UI
			 */
			// TextView resultText = (TextView)findViewById(R.id.SpinnerResult);
			// resultText.setText(SpinnerActivity.this.mSelection);
		}

		/**
		 * The definition of OnItemSelectedListener requires an override of
		 * onNothingSelected(), even though this implementation does not use it.
		 * 
		 * @param parent
		 *            - The View for this Listener
		 */
		public void onNothingSelected(AdapterView<?> parent) {

			// do nothing

		}
	}
}