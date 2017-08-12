package com.toroapp.toro.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.gms.vision.text.Text;
import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.fragment.Fragment_Barcode;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.ConnectivityStatus;
import com.toroapp.toro.utils.Font;
import com.toroapp.toro.fragment.Fragment_Manual;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final String MODULE = MainActivity.class.getSimpleName();
    private static String TAG = "";

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private AppCompatActivity mActivity;
    private Context mContext;
    private Font font = MyApplication.getInstance().getFontInstance();
    private FragmentManager mManager;

    private FrameLayout frame_container;
    private Toolbar toolbar;
    private Drawable drawableLogo;
    private static int tabIndex=0;
    private AHBottomNavigation bottomNavigation;
    private AHBottomNavigationItem ahbChooseByModel,ahbChooseByBarcode;
    private TextView text_view_title;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    private static String mNetworkInfo =null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        TAG="onCreate";
        Log.d(MODULE,TAG);

        setContentView(R.layout.activity_main);

        mActivity = this;
        mPreferences = this.getSharedPreferences(AppUtils.SHARED_PREFS, MODE_PRIVATE);
        mEditor = mPreferences.edit();
        mManager = getSupportFragmentManager();
        frame_container = (FrameLayout) findViewById(R.id.frame_container);
        drawableLogo = getResources().getDrawable(R.drawable.ic_logo);
        activityTitles = getResources().getStringArray(R.array.array_title_bottom);
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setupActionbar();
        loadFragment();
        setBottomNavigation();
    }

    public void setBottomNavigation()
    {
        Log.e(TAG,"setBottomNavigation");


        //create items
        ahbChooseByModel= new AHBottomNavigationItem(R.string.lbl_choose_by_model,R.drawable.ic_chooseby_model,R.color.colorWhite);
        ahbChooseByBarcode = new AHBottomNavigationItem(R.string.lbl_read_barcode,R.drawable.ic_chooseby_barcode,R.color.colorWhite);

        // Add items
        bottomNavigation.addItem(ahbChooseByModel);
        bottomNavigation.addItem(ahbChooseByBarcode);

        // Set background color
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FFFFFF"));
        bottomNavigation.setTitleTypeface(font.getHelveticaRegular());
        // Disable the translation inside the CoordinatorLayout
        bottomNavigation.setBehaviorTranslationEnabled(true);
        // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#C41230"));
        bottomNavigation.setInactiveColor(Color.parseColor("#80C41230"));
        // Force to tint the drawable (useful for font with icon for example)
        bottomNavigation.setForceTint(true);
        // Display color under navigation bar (API 21+)
        // Don't forget these lines in your style-v21
        bottomNavigation.setTranslucentNavigationEnabled(true);
        // Manage titles
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        // Use colored navigation with circle reveal effect
        bottomNavigation.setColored(false);

        // Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                // Do something cool here...
                switch (position)
                {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case 0:
                        tabIndex = 0;
                        break;
                    case 1:
                        tabIndex = 1;
                        break;
                }
                loadFragment();
                return true;
            }
        });
        loadFragment();
        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
            @Override public void onPositionChange(int y) {
                // Manage the new y position
            }
        });


    }

    public void loadFragment()
    {
        TAG="loadFragment";
        Log.d(MODULE,TAG);

        try
        {
            Fragment fragment = getFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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

    public void clearPreferences()
    {
        TAG="onStop";
        Log.d(MODULE,TAG);

        try
        {
            /*mEditor = mPreferences.edit();
            mEditor.putString(AppUtils.SHARED_LOGIN, "");
            mEditor.commit();*/
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        TAG="onCreateOptionsMenu";
        Log.d(MODULE,TAG);

        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_logo).setVisible(true);
        menu.findItem(R.id.action_logo).setIcon(drawableLogo);
        return true;
    }

    private Fragment getFragment()
    {
        TAG="getFragment";
        Log.d(MODULE,TAG);

        switch (tabIndex)
        {
            case 0:
                // manual testing
                return new Fragment_Manual();
            case 1:
                return new Fragment_Barcode();

            default:
                return new Fragment_Manual();
        }
    }


}
