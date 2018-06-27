package com.dldud.riceapp;

import android.annotation.SuppressLint;
import android.os.StrictMode;

/**
 * Created by dldud on 2018-03-25.
 */

public class NetworkUtil {
    @SuppressLint("NewApi")
    static public void setNetworkPolicy() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }
}
