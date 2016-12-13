package com.androidrecipes.ndkjni;

public class NativeLib {
    /**
     * Return the number of available cores on the device
     */
    public static native int getCpuCount();

    public static native String getCpuFamily();

    static {
        System.loadLibrary("features");
    }
}
