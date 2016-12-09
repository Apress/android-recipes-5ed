package com.androidrecipes.reachability;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ReachabilityManager {

    private ConnectivityManager mManager;

    private static ReachabilityManager instance;

    public static synchronized ReachabilityManager getInstance(Context context) {
        if (instance == null) {
            instance = new ReachabilityManager(context);
        }
        return instance;
    }

    private ReachabilityManager(Context context) {
        mManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isHostReachable(String hostName) {
        try {
            //Get int value from name
            byte[] ipv4 = InetAddress.getByName(hostName).getAddress();
            int address = ipv4[0];
            address += ipv4[1] << 8;
            address += ipv4[2] << 16;
            address += ipv4[3] << 24;
            return isHostReachable(address);
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public boolean isHostReachable(int hostAddress) {
        return (mManager.requestRouteToHost(ConnectivityManager.TYPE_WIFI, hostAddress)
                || mManager.requestRouteToHost(ConnectivityManager.TYPE_MOBILE, hostAddress));
    }

    public boolean isNetworkReachable() {
        NetworkInfo current = mManager.getActiveNetworkInfo();
        if (current == null) {
            return false;
        }
        return (current.getState() == NetworkInfo.State.CONNECTED);
    }
}
