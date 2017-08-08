package com.toroapp.toro.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.fragment.Fragment_Login;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.ConnectivityStatus;
import com.toroapp.toro.utils.Font;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private Handler mHandler;
    private String jsonResult;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private AppCompatActivity mActivity;
    private Context mContext;
    private Font font= MyApplication.getInstance().getFontInstance();
    private FragmentManager mManager;

    private FrameLayout frame_container;
    private Toolbar toolbar;
    private static List<BroadcastReceiver> receivers = new ArrayList<BroadcastReceiver>();
    private CollapsingToolbarLayout collapsing_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mActivity = this;
        mPreferences = this.getSharedPreferences(AppUtils.SHARED_PREFS,MODE_PRIVATE);
        mEditor = mPreferences.edit();
        mHandler = new Handler();
        this.registerReceiver(receiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        mManager = getSupportFragmentManager();
        getLoginData();
        //loadFragment();

    }
    public boolean isReceiverRegistered(BroadcastReceiver receiver){
        boolean registered = receivers.contains(receiver);
        Log.i(getClass().getSimpleName(), "is receiver "+receiver+" registered? "+registered);
        return registered;
    }

    private void getLoginData() {
        String loginData = mPreferences.getString(AppUtils.SHARED_LOGIN,null);
        if(loginData!=null && loginData.length()>0){
            Log.e(TAG,"getLoginData " + loginData.toString());
            Intent mainActivity = new Intent(this,MainActivity.class);
            startActivity(mainActivity);
            this.unregisterReceiver(receiver);
            finish();
        }else loadFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.registerReceiver(receiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        setupActionbar();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        boolean registered = isReceiverRegistered(receiver);
        Log.e(TAG,"onDestroy Check receiver : "+ registered);

        try {
            if(registered) this.unregisterReceiver(receiver);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected void onStop() {

        try {
            if(receiver!=null) this.unregisterReceiver(receiver);
        }catch (Exception ex){
            ex.printStackTrace();
        }super.onStop();
    }
    private void setupActionbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        TextView titleTextView = null;
        try
        {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            titleTextView = (TextView) f.get(toolbar);
            titleTextView.setTypeface(font.getHelveticaRegular());
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public void loadFragment()
    {
        Log.d(TAG,"loadFragment");
        setupActionbar();

        Runnable mPendingRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                // update the main content by replacing fragments
                Fragment fragment = new Fragment_Login();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame_container_login, fragment);
                fragmentTransaction.commit();
            }
        };

        if (mPendingRunnable != null)
        {
            mHandler.post(mPendingRunnable);
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(!ConnectivityStatus.isConnected(LoginActivity.this))
            {
                Log.e(TAG,"not connected");
                mEditor.putString(AppUtils.IS_NETWORK_AVAILABLE,AppUtils.NETWORK_NOT_AVAILABLE);
                mEditor.commit();
            }
            else
            {
                Log.e(TAG,"connected");
                mEditor.putString(AppUtils.IS_NETWORK_AVAILABLE,AppUtils.NETWORK_AVAILABLE);
                mEditor.commit();
            }
                    }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
