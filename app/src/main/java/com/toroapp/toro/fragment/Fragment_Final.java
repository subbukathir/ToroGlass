package com.toroapp.toro.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.Font;

/**
 * Created by subbu on 25/11/16.
 */

public class Fragment_Final extends Fragment implements View.OnClickListener
{
    private static final String TAG =Fragment_Final.class.getSimpleName();

    private AppCompatActivity mActivity;
    private Context mContext;
    private Font font= MyApplication.getInstance().getFontInstance();
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private FragmentManager mManager;
    private Handler mHandler;
    private Button btnContinue, btnSelectNewModel;
    private String mModelName=null;
    private CoordinatorLayout cl_main;
    private Toolbar mToolbar;
    private Bundle mArgs;
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
            mArgs = getArguments();
            if(mArgs!=null && mArgs.containsKey(AppUtils.ARGS_MODEL)){
                mModelName = mArgs.getString(AppUtils.ARGS_MODEL);
            }
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
            rootView = (View) inflater.inflate(R.layout.fragment_final,container,false);
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
            btnContinue = (Button) rootView.findViewById(R.id.btn_continue);
            btnSelectNewModel = (Button) rootView.findViewById(R.id.btn_select_new_model);

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
        btnContinue.setTypeface(font.getRobotoRegular());
        btnSelectNewModel.setTypeface(font.getRobotoRegular());
        btnContinue.setOnClickListener(this);
        btnSelectNewModel.setOnClickListener(this);
    }

    private void gotoFragment(Fragment fragment,String tag) {
        Bundle data = new Bundle();
        if(tag.equalsIgnoreCase(AppUtils.TAG_MANUAL)){
            data.putString(AppUtils.ARGS_MODEL,mModelName);
            fragment.setArguments(data);
        }
        FragmentTransaction fragmentTransaction = mManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        for(int i = 0; i < mManager.getBackStackEntryCount(); ++i) {
            mManager.popBackStack();
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_continue:
                gotoFragment(new Fragment_Manual(),AppUtils.TAG_MANUAL);
                break;
            case R.id.btn_select_new_model:
                gotoFragment(new Fragment_Manual(),AppUtils.TAG_FRAGMENT_INSPECTION);
                break;
        }
    }

}
