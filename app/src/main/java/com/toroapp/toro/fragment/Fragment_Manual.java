package com.toroapp.toro.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
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
import android.widget.ImageView;
import android.widget.TextView;

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

public class Fragment_Manual extends Fragment implements View.OnClickListener
{
    private static final String TAG =Fragment_Manual.class.getSimpleName();

    private AppCompatActivity mActivity;
    private Context mContext;
    private Font font= MyApplication.getInstance().getFontInstance();
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private FragmentManager mManager;
    private Handler mHandler;

    private AppCompatAutoCompleteTextView autoCompleteTextView;
    private String mStringJson=null,mModelName=null;
    private String[] strArrayModels;
    private ArrayList<String> listModel;

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
            mStringJson = mPreferences.getString(AppUtils.SHARED_LOGIN,null);
            mManager = mActivity.getSupportFragmentManager();
            font= MyApplication.getInstance().getFontInstance();
            //mArgs = getArguments().getString(ARGS_BUNDLE_MESSAGE);
            strArrayModels = getResources().getStringArray(R.array.array_model);
            listModel = new ArrayList<>(Arrays.asList(strArrayModels));

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
            rootView = (View) inflater.inflate(R.layout.fragment_manual,container,false);
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
            autoCompleteTextView = (AppCompatAutoCompleteTextView) rootView.findViewById(R.id.autoCompleteText);

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
    private void setProperties()
    {
        Log.e(TAG,"setProperties");
        autoCompleteTextView.setTypeface(font.getHelveticaRegular());

        ArrayAdapter mAdapter = new ArrayAdapter(mActivity,android.R.layout.simple_spinner_dropdown_item,listModel);
        autoCompleteTextView.setAdapter(mAdapter);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mModelName = listModel.get(i);
               final MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                        .customView(R.layout.dialog_selected_model,false)
                        .build();

                View view1 = dialog.getCustomView();
                ImageView iv_model;
                TextView tv_lbl_selected_model,tv_model_name;
                Button btnOkay,btnBack;
                iv_model = (ImageView) view1.findViewById(R.id.iv_selected_model);
                tv_lbl_selected_model = (TextView) view1.findViewById(R.id.tv_lbl_selected_model);
                tv_model_name = (TextView) view1.findViewById(R.id.tv_selected_model);
                btnBack = (Button) view1.findViewById(R.id.btnBack);
                btnOkay = (Button) view1.findViewById(R.id.btnOkay);
                tv_lbl_selected_model.setTypeface(font.getHelveticaRegular());
                tv_model_name.setTypeface(font.getHelveticaRegular());
                btnBack.setTypeface(font.getHelveticaRegular());
                btnBack.setTypeface(font.getHelveticaRegular());
                tv_model_name.setText(mModelName);
                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btnOkay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gotoFragmentInspection();
                        dialog.dismiss();
                    }
                });
                Log.e(TAG,"onItemClick "+ mModelName);
                dialog.show();
            }
        });
        Log.e(TAG,"text " + autoCompleteTextView.getText().toString());
    }

    private void gotoFragmentInspection() {
        Fragment fragment = new Fragment_Inspection();
        Bundle data = new Bundle();
        data.putString(AppUtils.ARGS_MODEL,mModelName);
        fragment.setArguments(data);
        FragmentTransaction fragmentTransaction = mManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame_container, fragment,AppUtils.TAG_FRAGMENT_INSPECTION);
        fragmentTransaction.addToBackStack(AppUtils.TAG_FRAGMENT_INSPECTION);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){


        }
    }

}
