package com.toroapp.toro.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by subbu on 7/7/17.
 */

public class ConnectivityStatus extends ContextWrapper {

    public ConnectivityStatus(Context base) {
        super(base);
    }

    public static boolean isConnected(Context context){

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo connection = manager.getActiveNetworkInfo();
        if (connection != null && connection.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }
}