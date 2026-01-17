package com.fajary.zygisk_overlayingviewui;

import android.util.Log;

public class Debug {
    public static final String tag = "fajary";
    public static void logI(String val)
    {
        Log.i(tag, val);
    }
    public static void logE(String val)
    {
        Log.e(tag, val);
    }
}
