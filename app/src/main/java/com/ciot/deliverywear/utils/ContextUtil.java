package com.ciot.deliverywear.utils;

import android.content.Context;

import androidx.annotation.NonNull;
public class ContextUtil {
    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    public static void setContext(@NonNull Context context) {
        sContext = context.getApplicationContext();
    }
}
