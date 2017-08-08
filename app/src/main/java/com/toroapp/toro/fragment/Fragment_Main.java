package com.toroapp.toro.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.Font;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by subbu on 25/11/16.
 */

public class Fragment_Main extends Fragment implements View.OnClickListener
{
    private static final String TAG =Fragment_Main.class.getSimpleName();

    private AppCompatActivity mActivity;
    private Context mContext;
    private Font font= MyApplication.getInstance().getFontInstance();
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private FragmentManager mManager;
    private Handler mHandler;
    private Button btnManual,btnBarcode;

    private CoordinatorLayout cl_main;
    private Toolbar mToolbar;
    private Snackbar snackbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG,"onCreate");

        super.onCreate(savedInstanceState);
        try{
            mActivity = (AppCompatActivity) getActivity();
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            mEditor = mPreferences.edit();
            mHandler = new Handler();
            mManager = mActivity.getSupportFragmentManager();
            font= MyApplication.getInstance().getFontInstance();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.e(TAG,"onCreateView");
        View rootView=null;
        try {
            rootView = (View) inflater.inflate(R.layout.fragment_main,container,false);
            initUI(rootView);
            setProperties();
        }catch (Exception ex)  {
            ex.printStackTrace();
        }

        return rootView;
    }
    private void initUI(View rootView){
        Log.e(TAG,"initUI");
        try {
            cl_main = (CoordinatorLayout) mActivity.findViewById(R.id.cl_main);
            btnManual = (Button) rootView.findViewById(R.id.btn_manual);
            btnBarcode = (Button) rootView.findViewById(R.id.btn_read_barcode);

            setupActionBar();
        }catch (Exception ex) {  ex.printStackTrace();  }
    }
    public void setupActionBar()
    {

        mToolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.app_name));
        mActivity.setSupportActionBar(mToolbar);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
    private void setProperties()    {
        Log.e(TAG,"setProperties");
        btnManual.setTypeface(font.getHelveticaRegular());
        btnBarcode.setTypeface(font.getHelveticaRegular());
        btnManual.setOnClickListener(this);
        btnBarcode.setOnClickListener(this);
    }

    private void gotoFragment(Fragment fragment,String tag) {
        FragmentTransaction fragmentTransaction = mManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame_container, fragment,tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_manual:
                gotoFragment(new Fragment_Manual(),AppUtils.TAG_MANUAL);
                break;
            case R.id.btn_read_barcode:
                gotoFragment(new Fragment_Barcode(),AppUtils.TAG_BARCODE);
                break;
        }
    }

}
