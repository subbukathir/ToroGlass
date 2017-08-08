package com.toroapp.toro;

import android.app.Application;

import com.toroapp.toro.utils.Font;


/**
 * Created by daemonsoft on 4/12/15.
 */

public class MyApplication extends Application
{
    public static final String TAG = MyApplication.class.getSimpleName();


    private static MyApplication mInstance;

    private Font font;
    //private MyPreferenceManager pref;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstance = this;

    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public Font getFontInstance()
    {
        if (this.font == null) {
            this.font=new Font(mInstance);
        }
        return this.font;
    }
}



