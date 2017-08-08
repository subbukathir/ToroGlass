package com.toroapp.toro.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import com.toroapp.toro.R;

import java.io.Reader;

/**
 * Created by subbu on 7/7/17.
 */

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = SplashScreen.class.getSimpleName();

    private static final int SPLASH_TIME_OUT = 3000;

    private Context mContext;
    Reader reader;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mContext = this;
        //new PrefetchJsonData().execute();

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }


    private class PrefetchJsonData extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(SplashScreen.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        protected Void doInBackground(Void... agrs) {


            try {

            } catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }
    }
}
