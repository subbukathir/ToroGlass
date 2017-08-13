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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.adapter.ViewInspectionListAdapter;
import com.toroapp.toro.components.SimpleDividerItemDecoration;
import com.toroapp.toro.listeners.ClickListener;
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

public class Fragment_ViewInspectionList extends Fragment implements InspectionDataListener, View.OnClickListener, ClickListener {
    private static final String TAG = Fragment_ViewInspectionList.class.getSimpleName();
    public Bundle mSavedInstanceState;
    Font font = MyApplication.getInstance().getFontInstance();
    AppCompatActivity mActivity;
    FragmentManager mManager;
    Toolbar mToolbar;
    CoordinatorLayout cl_main;
    RecyclerView recyclerView;
    LinearLayoutManager mLayoutManager;
    TextView text_view_loading_message;
    LinearLayout layout_loading_message;
    TextView text_view_message, text_view_empty, tv_select_model, tv_select_vehicle_id;
    LinearLayout layout_loading;
    RelativeLayout layout_empty;
    ViewInspectionListAdapter mAdapter;
    List<InspectionEntity> mInspectionList = new ArrayList<>();
    Bundle mArgs;
    SharedPreferences mPreferences;
    InspectionDbInitializer inspectionDb;
    private List<String> mVehicleList = new ArrayList<>();
    private List<String> mModelList = new ArrayList<>();
    Snackbar snackbar;
    private String mStrEmpId = null, mModelName = null, mVehicleId = null;
    private String mLoginData = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Log.d(TAG, "onCreate");
            mActivity = (AppCompatActivity) getActivity();
            setHasOptionsMenu(true);
            setRetainInstance(false);
            mManager = mActivity.getSupportFragmentManager();
            mSavedInstanceState = savedInstanceState;
            inspectionDb = new InspectionDbInitializer(this);
            inspectionDb.getAllModelNames(AppDatabase.getAppDatabase(mActivity),AppUtils.MODE_GETALL_MODEL);
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
        Log.d(TAG, "onCreateView");
        initView(rootView);
        return rootView;
    }

    public void initView(View view) {
        Log.d(TAG, "initView");
        try {
            cl_main = (CoordinatorLayout) mActivity.findViewById(R.id.cl_main);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            tv_select_model = (TextView) view.findViewById(R.id.tv_select_model_name);
            tv_select_vehicle_id = (TextView) view.findViewById(R.id.tv_select_vehicle_id);
            text_view_loading_message = (TextView) view.findViewById(R.id.text_view_message);
            layout_loading_message = (LinearLayout) view.findViewById(R.id.layout_loading);

            layout_loading = (LinearLayout) view.findViewById(R.id.layout_loading);
            layout_empty = (RelativeLayout) view.findViewById(R.id.layout_empty);
            text_view_empty = (TextView) view.findViewById(R.id.text_view_empty);
            text_view_message = (TextView) view.findViewById(R.id.text_view_message);
            setupActionBar();
            setProperties();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setupActionBar() {
        mToolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.lbl_report));
        mActivity.setSupportActionBar(mToolbar);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void setProperties() {
        Log.d(TAG, "setProperties");
        try {
            setManager();
            text_view_empty.setTypeface(font.getRobotoRegular());
            text_view_message.setTypeface(font.getRobotoRegular());
            tv_select_model.setTypeface(font.getRobotoRegular());

            text_view_loading_message.setTypeface(font.getRobotoRegular());
            //getSearchInspectionData();
            tv_select_model.setOnClickListener(this);
            tv_select_vehicle_id.setOnClickListener(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setManager() {
        Log.d(TAG, "setManager");
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
        if (view.getId() == R.id.tv_select_model_name) {
            getModelName();
        }
        if (view.getId() == R.id.tv_select_vehicle_id) {
            getVehicleId();
        }
    }

    public void getVehicleId() {
        Log.e(TAG, "getVehicleId");
        try {
            if (mVehicleList.size() > 0) {
                new MaterialDialog.Builder(mActivity)
                        .title(R.string.lbl_select_vehicle_id)
                        .items(mVehicleList)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (which >= 0) {
                                    mVehicleId = text.toString();
                                    inspectionDb.getAllDataByVehicleId(AppDatabase.getAppDatabase(mActivity), mModelName, mVehicleId, AppUtils.MODE_GETALL_USING_VEHICLE);
                                    tv_select_vehicle_id.setText(text.toString());
                                    tv_select_vehicle_id.setTypeface(font.getRobotoBold());
                                }
                                return true;
                            }
                        })
                        .positiveText(R.string.lbl_choose)
                        .show();
            } else AppUtils.showDialog(mActivity, getString(R.string.msg_select_model_name));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getModelName() {
        Log.e(TAG, "getModelName");
        try {
            if(mModelList.size()>0){
                new MaterialDialog.Builder(mActivity)
                        .title(R.string.lbl_select_model)
                        .items(mModelList)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (which >= 0) {
                                    mModelName = text.toString();
                                    inspectionDb.getAllDataByModelName(AppDatabase.getAppDatabase(mActivity), mModelName, AppUtils.MODE_GETALL_USING_MODEL);
                                    tv_select_model.setText(text.toString());
                                    tv_select_model.setTypeface(font.getRobotoBold());
                                    tv_select_vehicle_id.setText(getString(R.string.lbl_select_vehicle_id));
                                }
                                return true;
                            }
                        })
                        .positiveText(R.string.lbl_choose)
                        .show();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getSearchInspectionData() {
        Log.d(TAG, "getSearchInspectionData");
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
        Log.d(TAG, "setSearchInspectionList");

        try {
            Log.w(TAG, " mInspectionList : " + mInspectionList.size());
            if (mInspectionList.size() > 0) {
                mAdapter = new ViewInspectionListAdapter(mActivity, mInspectionList);
                mAdapter.setListener(this);
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
        Log.d(TAG, "showLoadingSearchInspection");

        try {
            layout_empty.setVisibility(View.GONE);
            layout_loading.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void showEmptyView(String Str_Msg) {
        Log.d(TAG, "showEmptyView");

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
        Log.e(TAG, "onDataReceivedSuccess");
        mInspectionList = inspectionEntityList;
        getSearchInspectionData();
    }

    @Override
    public void onDataReceivedErr(String strErr) {
        Log.e(TAG, "onDataReceivedErr " + strErr);
        showEmptyView(strErr);
        mVehicleList.clear();
        tv_select_vehicle_id.setBackground(getResources().getDrawable(R.drawable.bg_border_spinner_red));
        tv_select_vehicle_id.setText(strErr);
    }

    @Override
    public void onVehicleListSuccess(List<String> vehicleList, int mode) {
        if (mode == AppUtils.MODE_GETALL_USING_MODEL) {
            mVehicleList = vehicleList;
            showEmptyView(getString(R.string.lbl_select_vehicle_id));
            tv_select_vehicle_id.setBackground(getResources().getDrawable(R.drawable.bg_border_spinner_red));
            tv_select_vehicle_id.setText(getString(R.string.lbl_select_vehicle_id));
            tv_select_vehicle_id.setPadding(10, 10, 10, 10);
        }else if (mode == AppUtils.MODE_GETALL_MODEL){
            mModelList = vehicleList;
        }

    }

    @Override
    public void onClick(View view, int position) {
        Log.e(TAG, "onClick");
        showImageInLarge(position);
    }

    private void showImageInLarge(int position) {
        final MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .customView(R.layout.view_dialog_image_large, false)
                .build();
        View dialogView = dialog.getCustomView();
        ImageView iv_large;
        iv_large = (ImageView) dialogView.findViewById(R.id.iv_larger);
        if (mInspectionList.get(position).getImageData() != null)
            iv_large.setImageBitmap(AppUtils.decodeBase64toImage(mInspectionList.get(position).getImageData()));
        else iv_large.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        dialog.show();
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}
