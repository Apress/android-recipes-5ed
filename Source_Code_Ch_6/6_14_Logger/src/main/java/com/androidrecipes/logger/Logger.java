package com.androidrecipes.logger;

import android.util.Log;

public class Logger {
    private static final String LOGTAG = "AndroidRecipes";

    private static String getLogString(String format, Object... args) {
        //Minor optimization, only call String.format if necessary
        if (args.length == 0) {
            return format;
        }

        return String.format(format, args);
    }

    /* The INFO, WARNING, ERROR log levels print always */

    public static void e(String format, Object... args) {
        Log.e(LOGTAG, getLogString(format, args));
    }

    public static void w(String format, Object... args) {
        Log.w(LOGTAG, getLogString(format, args));
    }

    public static void w(Throwable throwable) {
        Log.w(LOGTAG, throwable);
    }

    public static void i(String format, Object... args) {
        Log.i(LOGTAG, getLogString(format, args));
    }

    /* The DEBUG and VERBOSE log levels are protected by DEBUG flag */

    public static void d(String format, Object... args) {
        if (!BuildConfig.DEBUG) return;

        Log.d(LOGTAG, getLogString(format, args));
    }

    public static void v(String format, Object... args) {
        if (!BuildConfig.DEBUG) return;

        Log.v(LOGTAG, getLogString(format, args));
    }
}
