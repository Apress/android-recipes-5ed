package com.androidrecipes.reachability;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class Reachability extends Activity {

    ReachabilityManager mReach;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mReach = ReachabilityManager.getInstance(this);
        //mReach.isHostReachable("209.85.227.104")
        Toast.makeText(this, "Network " + mReach.isNetworkReachable() + "\nGoogle " + mReach.isHostReachable(0xD155E368), Toast.LENGTH_SHORT).show();
    }
}