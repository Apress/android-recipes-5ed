package com.androidrecipes.restrictedprofiles;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.os.Bundle;
import android.os.UserManager;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class RestrictionsReceiver extends BroadcastReceiver {

    public static final String RESTRICTION_PURCHASE = "purchases";
    public static final String RESTRICTION_AGERANGE = "age_range";

    private static final String[] AGES = {"3+", "5+", "10+", "18+"};
    private static final String[] AGE_VALUES = {"3", "5", "10", "18"};

    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<RestrictionEntry> restrictions = new ArrayList<RestrictionEntry>();

        RestrictionEntry purchase = new RestrictionEntry(RESTRICTION_PURCHASE, false);
        purchase.setTitle("Content Purchases");
        purchase.setDescription("Allow purchasing of content in the application.");
        restrictions.add(purchase);

        RestrictionEntry ages = new RestrictionEntry(RESTRICTION_AGERANGE, AGE_VALUES[0]);
        ages.setTitle("Age Level");
        ages.setDescription("Difficulty level for application content.");
        ages.setChoiceEntries(AGES);
        ages.setChoiceValues(AGE_VALUES);
        restrictions.add(ages);

        Bundle result = new Bundle();
        result.putParcelableArrayList(Intent.EXTRA_RESTRICTIONS_LIST, restrictions);

        setResultExtras(result);
    }

    /*
     * Utility to get readable strings from restriction keys
     */
    public static String getNameForRestriction(String key) {
        if (UserManager.DISALLOW_CONFIG_BLUETOOTH.equals(key)) {
            return "Unable to configure Bluetooth";
        }
        if (UserManager.DISALLOW_CONFIG_CREDENTIALS.equals(key)) {
            return "Unable to configure user credentials";
        }
        if (UserManager.DISALLOW_CONFIG_WIFI.equals(key)) {
            return "Unable to configure Wifi";
        }
        if (UserManager.DISALLOW_INSTALL_APPS.equals(key)) {
            return "Unable to install applications";
        }
        if (UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES.equals(key)) {
            return "Unable to enable unknown sources";
        }
        if (UserManager.DISALLOW_MODIFY_ACCOUNTS.equals(key)) {
            return "Unable to modify accounts";
        }
        if (UserManager.DISALLOW_REMOVE_USER.equals(key)) {
            return "Unable to remove users";
        }
        if (UserManager.DISALLOW_SHARE_LOCATION.equals(key)) {
            return "Unable to toggle location sharing";
        }
        if (UserManager.DISALLOW_UNINSTALL_APPS.equals(key)) {
            return "Unable to uninstall applications";
        }
        if (UserManager.DISALLOW_USB_FILE_TRANSFER.equals(key)) {
            return "Unable to transfer files";
        }

        return "Unknown Restriction: " + key;
    }
}
