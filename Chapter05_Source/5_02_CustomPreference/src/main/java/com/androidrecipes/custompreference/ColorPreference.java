package com.androidrecipes.custompreference;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

public class ColorPreference extends DialogPreference {

    private static final int DEFAULT_COLOR = Color.WHITE;
    /* Local copy of the current color setting */
    private int mCurrentColor;
    /* Sliders to set color components */
    private SeekBar mRedLevel, mGreenLevel, mBlueLevel;

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*
     * Called to construct a new dialog to show when the preference
     * is clicked.  We create and set up a new content view for
     * each instance.
     */
    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        //Create the dialog's content view
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.preference_color, null);
        mRedLevel = (SeekBar) rootView.findViewById(R.id.selector_red);
        mGreenLevel = (SeekBar) rootView.findViewById(R.id.selector_green);
        mBlueLevel = (SeekBar) rootView.findViewById(R.id.selector_blue);

        mRedLevel.setProgress(Color.red(mCurrentColor));
        mGreenLevel.setProgress(Color.green(mCurrentColor));
        mBlueLevel.setProgress(Color.blue(mCurrentColor));

        //Attach the content view
        builder.setView(rootView);
        super.onPrepareDialogBuilder(builder);
    }

    /*
     * Called when the dialog is closed with the result of
     * the button tapped by the user.
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            //When OK is pressed, obtain and save the color value
            int color = Color.rgb(
                    mRedLevel.getProgress(),
                    mGreenLevel.getProgress(),
                    mBlueLevel.getProgress());
            setCurrentValue(color);
        }
    }

    /*
     * Called by the framework to obtain the default value
     * passed in the preference XML definition
     */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        //Return the default value from XML as a color int
        ColorStateList value = a.getColorStateList(index);
        if (value == null) {
            return DEFAULT_COLOR;
        }
        return value.getDefaultColor();
    }

    /*
     * Called by the framework to set the initial value of the
     * preference, either from it's default or the last persisted
     * value.
     */
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setCurrentValue(restorePersistedValue ? getPersistedInt(DEFAULT_COLOR) : (Integer) defaultValue);
    }

    /*
     * Return a custom summary based on the current setting
     */
    @Override
    public CharSequence getSummary() {
        //Construct the summary with the color value in hex
        int color = getPersistedInt(DEFAULT_COLOR);
        String content = String.format("Current Value is 0x%02X%02X%02X",
                Color.red(color), Color.green(color), Color.blue(color));
        //Return the summary text as a Spannable, colored by the selection
        Spannable summary = new SpannableString(content);
        summary.setSpan(new ForegroundColorSpan(color), 0, summary.length(), 0);
        return summary;
    }

    private void setCurrentValue(int value) {
        //Update latest value
        mCurrentColor = value;

        //Save new value
        persistInt(value);
        //Notify preference listeners
        notifyDependencyChange(shouldDisableDependents());
        notifyChanged();
    }

}
