package com.androidrecipes.restrictedprofiles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements
        OnSeekBarChangeListener, OnCheckedChangeListener {

    private Button mPurchaseButton;
    private DrawingView mDrawingView;
    private SeekBar mFullSlider;
    private RadioGroup mSimpleSelector;

    /* Profile Restriction Values */
    private boolean mHasPurchases;
    private int mMinAge;
    /* Content Purchase Flags */
    private boolean mHasCanvasColors = false;
    private boolean mHasPaintColors = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPurchaseButton = (Button) findViewById(R.id.button_purchase);
        mDrawingView = (DrawingView) findViewById(R.id.drawing_surface);
        mFullSlider = (SeekBar) findViewById(R.id.full_slider);
        mSimpleSelector = (RadioGroup) findViewById(R.id.simple_selector);

        mFullSlider.setOnSeekBarChangeListener(this);
        mSimpleSelector.setOnCheckedChangeListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            UserManager manager = (UserManager) getSystemService(USER_SERVICE);
            //Check for system-level restrictions
            Bundle restrictions = manager.getUserRestrictions();
            if (restrictions != null && !restrictions.isEmpty()) {
                showSystemRestrictionsDialog(restrictions);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
         * Restrictions may change while the app is in the background so we need
         * to check this each time we return
         */
        updateRestrictions();
        // Update UI based on restriction changes
        updateDisplay();
    }

    public void onPurchaseClick(View v) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle("Content Upgrades")
                .setMessage(
                        "Tap any of the following items to add them.")
                .setPositiveButton("Canvas Colors $2.99",
                        mPurchaseListener)
                .setNeutralButton("Paint Colors $2.99",
                        mPurchaseListener)
                .setNegativeButton("Both Items $4.99",
                        mPurchaseListener).show();
    }

    private DialogInterface.OnClickListener mPurchaseListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            mHasCanvasColors = true;
                            break;
                        case DialogInterface.BUTTON_NEUTRAL:
                            mHasPaintColors = true;
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            mHasCanvasColors = true;
                            mHasPaintColors = true;
                            break;
                    }
                    Toast.makeText(getApplicationContext(), "Thank You For Your Purchase!",
                            Toast.LENGTH_SHORT).show();
                    updateDisplay();
                }
            };

    private void showSystemRestrictionsDialog(Bundle restrictions) {
        StringBuilder message = new StringBuilder();
        for (String key : restrictions.keySet()) {
            //Make sure the value of the restriction is true
            if (restrictions.getBoolean(key)) {
                message.append(RestrictionsReceiver.getNameForRestriction(key));
                message.append("\n");
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("System Restrictions")
                .setMessage(message.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        float width;
        switch (checkedId) {
            default:
            case R.id.option_small:
                width = 4f;
                break;
            case R.id.option_medium:
                width = 12f;
                break;
            case R.id.option_large:
                width = 25f;
                break;
            case R.id.option_xlarge:
                width = 45f;
                break;
        }
        mDrawingView.setStrokeWidth(width);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        mDrawingView.setStrokeWidth(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private void updateDisplay() {
        //Show/hide purchase button
        mPurchaseButton.setVisibility(
                mHasPurchases ? View.VISIBLE : View.GONE);

        //Update age-restricted content
        mFullSlider.setVisibility(View.GONE);
        mSimpleSelector.setVisibility(View.GONE);
        switch (mMinAge) {
            case 18:
                //Full-range slider
                mFullSlider.setVisibility(View.VISIBLE);
                mFullSlider.setProgress(4);
                break;
            case 10:
                //Four options
                mSimpleSelector.setVisibility(View.VISIBLE);
                findViewById(R.id.option_medium).setVisibility(View.VISIBLE);
                findViewById(R.id.option_xlarge).setVisibility(View.VISIBLE);
                mSimpleSelector.check(R.id.option_medium);
                break;
            case 5:
                //Big/small option
                mSimpleSelector.setVisibility(View.VISIBLE);
                findViewById(R.id.option_medium).setVisibility(View.GONE);
                findViewById(R.id.option_xlarge).setVisibility(View.GONE);
                mSimpleSelector.check(R.id.option_small);
                break;
            case 3:
            default:
                //No selection
                break;
        }

        //Update display with purchases
        mDrawingView.setPaintColor(mHasPaintColors ? Color.BLUE : Color.GRAY);
        mDrawingView.setCanvasColor(mHasCanvasColors ? Color.GREEN : Color.TRANSPARENT);
    }

    private void updateRestrictions() {
        // Check for restrictions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            UserManager manager = (UserManager) getSystemService(USER_SERVICE);
            Bundle restrictions = manager
                    .getApplicationRestrictions(getPackageName());
            if (restrictions != null) {
                // Read restriction settings
                mHasPurchases = restrictions.getBoolean(
                        RestrictionsReceiver.RESTRICTION_PURCHASE, true);
                try {
                    mMinAge = Integer.parseInt(restrictions.getString(
                            RestrictionsReceiver.RESTRICTION_AGERANGE, "18"));
                } catch (NumberFormatException e) {
                    mMinAge = 0;
                }
            } else {
                // We have no restrictions
                mHasPurchases = true;
                mMinAge = 18;
            }
        } else {
            // We are not on a system that supports restrictions
            mHasPurchases = true;
            mMinAge = 18;
        }
    }
}
