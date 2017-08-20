package com.toroapp.toro.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.toroapp.toro.fragment.Fragment_Username;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.Font;

public class LoginActivity extends AppCompatActivity
{
    private static final String MODULE = LoginActivity.class.getSimpleName();
    private static String TAG = "";

    private String jsonResult;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private AppCompatActivity mActivity;
    private Context mContext;
    private Font font= MyApplication.getInstance().getFontInstance();
    private FragmentManager mManager;

    private FrameLayout frame_container;
    private Toolbar toolbar;
    private TextView text_view_title;
    private CollapsingToolbarLayout collapsing_toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        TAG="onCreate";
        Log.d(MODULE,TAG);

        setContentView(R.layout.activity_login);

        mActivity = this;
        mPreferences = this.getSharedPreferences(AppUtils.SHARED_PREFS,MODE_PRIVATE);
        mEditor = mPreferences.edit();
        mManager = getSupportFragmentManager();


        getLoginData();
    }

    private void getLoginData()
    {
        TAG="getLoginData";
        Log.d(MODULE,TAG);

        String loginData = mPreferences.getString(AppUtils.SHARED_LOGIN,null);
        if(loginData!=null && loginData.length()>0)
        {
            Log.e(TAG,"getLoginData " + loginData.toString());
            Intent mainActivity = new Intent(this,MainActivity.class);
            startActivity(mainActivity);
            finish();
        }
        else loadFragment();
    }

    private void setupActionbar()
    {
        TAG="onStop";
        Log.d(MODULE,TAG);

        try
        {
            text_view_title = (TextView) findViewById(R.id.text_view_title);
            text_view_title.setTypeface(font.getRobotoRegular());
            text_view_title.setText(getString(R.string.app_name));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public void loadFragment()
    {
        Log.d(TAG,"loadFragment");
        //setupActionbar();

        try
        {
            Fragment fragment = new Fragment_Username();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame_container_login, fragment);
            fragmentTransaction.commit();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }




}
