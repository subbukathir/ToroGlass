package com.toroapp.toro.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.adapter.ViewInspectionListAdapter;
import com.toroapp.toro.components.SimpleDividerItemDecoration;
import com.toroapp.toro.listeners.InspectionDataListener;
import com.toroapp.toro.localstorage.database.AppDatabase;
import com.toroapp.toro.localstorage.dbhelper.InspectionDbInitializer;
import com.toroapp.toro.localstorage.entity.InspectionEntity;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.Font;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daemonsoft on 7/18/2017.
 */

public class Fragment_ViewInspectionList extends Fragment implements InspectionDataListener,View.OnClickListener {
    private static final String TAG = Fragment_ViewInspectionList.class.getSimpleName();
    public Bundle mSavedInstanceState;
    Font font = MyApplication.getInstance().getFontInstance();
    AppCompatActivity mActivity;
    FragmentManager mManager;
    CoordinatorLayout cl_main;
    RecyclerView recyclerView;
    LinearLayoutManager mLayoutManager;
    TextView text_view_loading_message;
    LinearLayout layout_loading_message;
    TextView text_view_message, text_view_empty,tv_select_model;
    LinearLayout layout_loading;
    RelativeLayout layout_empty;
    ViewInspectionListAdapter mAdapter;
    List<InspectionEntity> mInspectionList = new ArrayList<>();
    Bundle mArgs;
    SharedPreferences mPreferences;
    InspectionDbInitializer inspectionDb;
    Snackbar snackbar;
    private String mStrEmpId = null,mModelName=null;
    private String mLoginData = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Log.d(TAG,"onCreate");
            mActivity = (AppCompatActivity) getActivity();
            setHasOptionsMenu(true);
            setRetainInstance(false);
            mManager = mActivity.getSupportFragmentManager();
            mSavedInstanceState = savedInstanceState;
            inspectionDb = new InspectionDbInitializer(this);
            mArgs = getArguments();
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            mLoginData = mPreferences.getString(AppUtils.SHARED_LOGIN, null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_inspection_list, container, false);
        Log.d(TAG,"onCreateView");
        initView(rootView);
        return rootView;
    }

    public void initView(View view) {
        Log.d(TAG,"initView");
        try {
            cl_main = (CoordinatorLayout) mActivity.findViewById(R.id.cl_main);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            tv_select_model = (TextView) view.findViewById(R.id.tv_select_model_name);
            text_view_loading_message = (TextView) view.findViewById(R.id.text_view_message);
            layout_loading_message = (LinearLayout) view.findViewById(R.id.layout_loading);

            layout_loading = (LinearLayout) view.findViewById(R.id.layout_loading);
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
            text_view_message = (TextView) view.findViewById(R.id.text_view_message);

            setProperties();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setProperties() {
        Log.d(TAG,"setProperties");
        try {
            setManager();
            text_view_empty.setTypeface(font.getHelveticaRegular());
            text_view_message.setTypeface(font.getHelveticaRegular());
            tv_select_model.setTypeface(font.getHelveticaRegular());

            text_view_loading_message.setTypeface(font.getHelveticaRegular());
            //getSearchInspectionData();
            tv_select_model.setOnClickListener(this);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setManager() {
        Log.d(TAG,"setManager");
        try {
            mLayoutManager = new LinearLayoutManager(mActivity);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new SimpleDividerItemDecoration(mActivity));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.tv_select_model_name){
            getModelName();
        }
    }

    public void getModelName(){
        Log.e(TAG,"getDate");
        try
        {
            new MaterialDialog.Builder(mActivity)
                    .title(R.string.lbl_select_model)
                    .items(R.array.array_model)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            if(which>=0 ){
                                mModelName = text.toString();
                                inspectionDb.getAllDataByModelName(AppDatabase.getAppDatabase(mActivity),mModelName,AppUtils.MODE_GETALL_USING_MODEL);
                                tv_select_model.setText(text.toString());
                                tv_select_model.setTypeface(font.getHelveticaBold());
                            }
                            return true;
                        }
                    })
                    .positiveText(R.string.lbl_choose)
                    .show();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    public void getSearchInspectionData() {
        Log.d(TAG,"getSearchInspectionData");
        try {
            if (snackbar != null) snackbar.dismiss();
            showLoadingSearchInspection();
            //mList = mArgs.getParcelableArrayList(AppUtils.ARGS_MULTISEARC_COMPLAINT_LIST);
            setSearchInspectionList();
            mAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setSearchInspectionList() {
        Log.d(TAG,"setSearchInspectionList");

        try {
            Log.w(TAG , " mInspectionList : " + mInspectionList.size());
            if (mInspectionList.size() > 0) {
                mAdapter = new ViewInspectionListAdapter(mActivity, mInspectionList);
                recyclerView.setAdapter(mAdapter);
                showList();
            } else {
                showError(getString(R.string.msg_receive_inspection_empty));
                showEmptyView(getString(R.string.msg_receive_inspection_empty));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showList() {
        recyclerView.setVisibility(View.VISIBLE);
        layout_loading.setVisibility(View.GONE);
        layout_empty.setVisibility(View.GONE);
    }

    public void showLoadingSearchInspection() {
        Log.d(TAG,"showLoadingSearchInspection");

        try {
            layout_empty.setVisibility(View.GONE);
            layout_loading.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void showEmptyView(String Str_Msg) {
        Log.d(TAG,"showEmptyView");

        try {
            text_view_empty.setText(Str_Msg);
            layout_empty.setVisibility(View.VISIBLE);
            layout_loading.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void showError(String Str_Msg) {
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        snackbar = Snackbar
                .make(cl_main, Str_Msg, Snackbar.LENGTH_SHORT)
                .setAction(getString(R.string.lbl_retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getSearchInspectionData();
                    }
                });
        snackbar.setDuration(9000);
        snackbar.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (snackbar != null) snackbar.dismiss();
    }

    @Override
    public void onDataReceivedSuccess(List<InspectionEntity> inspectionEntityList) {
        Log.e(TAG,"onDataReceivedSuccess");
        mInspectionList = inspectionEntityList;
        getSearchInspectionData();
    }

    @Override
    public void onDataReceivedErr(String strErr) {
        Log.e(TAG,"onDataReceivedErr " + strErr);
        showEmptyView(strErr);
    }
}
