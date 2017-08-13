package com.toroapp.toro.fragment;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.popalay.tutors.TutorialListener;
import com.popalay.tutors.Tutors;
import com.popalay.tutors.TutorsBuilder;
import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.activities.MainActivity;
import com.toroapp.toro.listeners.InspectionDataListener;
import com.toroapp.toro.localstorage.database.AppDatabase;
import com.toroapp.toro.localstorage.dbhelper.InspectionDbInitializer;
import com.toroapp.toro.localstorage.entity.InspectionEntity;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.Font;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by subbu on 25/11/16.
 */

public class Fragment_Manual extends Fragment implements View.OnClickListener,InspectionDataListener,TutorialListener
{
    private static final String MODULE = MainActivity.class.getSimpleName();
    private static String TAG = "";

    private AppCompatActivity mActivity;
    private Context mContext;
    private Font font= MyApplication.getInstance().getFontInstance();
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private FragmentManager mManager;
    private Handler mHandler;

    private AppCompatAutoCompleteTextView autoCompleteTextView;
    private TextInputLayout til_vehicleId;
    private TextInputEditText tie_vehicleId;
    private Button btnInspect;
    private String mStringJson=null,mModelName="",mVehicleId=null;
    private String[] strArrayModels;
    private ArrayList<String> listModel;
    private List<String> listVehicleId = new ArrayList<>();
    private Bundle mArgs;
    private CoordinatorLayout cl_main;
    private Toolbar mToolbar;
    private Snackbar snackbar;
    //showcase
    private ImageView iv_info;
    private Map<String, View> tutorials;
    private Iterator<Map.Entry<String, View>> iterator;
    private Tutors tutors;
    private InspectionDbInitializer mInspectionDb;
    private AHBottomNavigation mBottomNavigation;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        TAG="onCreate";
        Log.d(MODULE,TAG);

        try
        {
            mActivity = (AppCompatActivity) getActivity();
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            mEditor = mPreferences.edit();
            mInspectionDb = new InspectionDbInitializer(this);
            mHandler = new Handler();
            mStringJson = mPreferences.getString(AppUtils.SHARED_LOGIN,null);
            mManager = mActivity.getSupportFragmentManager();
            font= MyApplication.getInstance().getFontInstance();
            mArgs = getArguments();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        TAG="onCreateView";
        Log.d(MODULE,TAG);

        View rootView=null;
        try {
            rootView = (View) inflater.inflate(R.layout.fragment_manual,container,false);
            initUI(rootView);
            initTutorials();
            setProperties();
        }catch (Exception ex)  {
            ex.printStackTrace();
        }

        return rootView;
    }

    private void initUI(View rootView)
    {
        TAG="initUI";
        Log.d(MODULE,TAG);

        try
        {
            cl_main = (CoordinatorLayout) mActivity.findViewById(R.id.cl_main);
            mBottomNavigation = (AHBottomNavigation) mActivity.findViewById(R.id.bottom_navigation);
            mBottomNavigation.setVisibility(View.VISIBLE);
            autoCompleteTextView = (AppCompatAutoCompleteTextView) rootView.findViewById(R.id.autoCompleteText);
            til_vehicleId = (TextInputLayout) rootView.findViewById(R.id.til_vehicle_id);
            tie_vehicleId = (TextInputEditText) rootView.findViewById(R.id.tie_vehicle_id);
            btnInspect = (Button) rootView.findViewById(R.id.btnInspect);
            iv_info = (ImageView) rootView.findViewById(R.id.iv_info);
            setupActionBar();
        }catch (Exception ex) {  ex.printStackTrace();  }
    }
    public void setupActionBar()    {
        mToolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.app_name));
        mActivity.setSupportActionBar(mToolbar);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
    private void setProperties()
    {
        TAG="setProperties";
        Log.d(MODULE,TAG);

        autoCompleteTextView.setTypeface(font.getRobotoRegular());
        tie_vehicleId.addTextChangedListener(new Fragment_Manual.MyTextWatcher(tie_vehicleId));
        if(mArgs!=null && mArgs.containsKey(AppUtils.ARGS_MODEL)){
            mModelName = mArgs.getString(AppUtils.ARGS_MODEL);
            mInspectionDb.getAllDataByModelName(AppDatabase.getAppDatabase(mActivity),mModelName,AppUtils.MODE_GETALL_USING_MODEL);
            autoCompleteTextView.setText(mModelName);
            autoCompleteTextView.setEnabled(false);
            autoCompleteTextView.setClickable(false);
            tie_vehicleId.setFocusable(true);
        }else {
            autoCompleteTextView.setHint(getString(R.string.lbl_select_model));
            autoCompleteTextView.setEnabled(true);
            autoCompleteTextView.setClickable(true);
        }
        btnInspect.setOnClickListener(this);
        ArrayAdapter mAdapter = new ArrayAdapter(mActivity,android.R.layout.simple_spinner_dropdown_item,listModel);
        autoCompleteTextView.setAdapter(mAdapter);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setTypeface(font.getRobotoRegular());
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
                tv_lbl_selected_model.setTypeface(font.getRobotoRegular());
                tv_model_name.setTypeface(font.getRobotoRegular());
                btnBack.setTypeface(font.getRobotoRegular());
                btnBack.setTypeface(font.getRobotoRegular());
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
                        //gotoFragmentInspection();
                        dialog.dismiss();
                        mInspectionDb.getAllDataByModelName(AppDatabase.getAppDatabase(mActivity),mModelName,AppUtils.MODE_GETALL_USING_MODEL);
                    }
                });
                Log.e(TAG,"onItemClick "+ mModelName);
                dialog.show();
            }
        });
        Log.e(TAG,"text " + autoCompleteTextView.getText().toString());
        //show case
        iv_info.setOnClickListener(this);
        tutors = new TutorsBuilder()
                .textColorRes(android.R.color.white)
                .shadowColorRes(R.color.colorBlue)
                .textSizeRes(R.dimen.textNormal)
                .completeIconRes(R.drawable.ic_cross_24_white)
                .spacingRes(R.dimen.spacingNormal)
                .lineWidthRes(R.dimen.lineWidth)
                .cancelable(false)
                .build();

        tutors.setListener(this);

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Fragment_Manual.this.iv_info.setAlpha((Float) animation.getAnimatedValue());
            }
        });

        animator.setDuration(500);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(-1);
        animator.start();

    }

    private void gotoFragmentInspection() {
        Fragment fragment = new Fragment_Inspection();
        Bundle data = new Bundle();
        data.putString(AppUtils.ARGS_MODEL,mModelName);
        data.putString(AppUtils.ARGS_VEHICLEID,mVehicleId);
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
            case R.id.btnInspect:
                submitForm();
                break;
            case R.id.iv_info:
                iterator = tutorials.entrySet().iterator();
                showTutorial(iterator);
                break;
        }
    }

    private void submitForm() {
        hideKeyboard();
        mModelName = autoCompleteTextView.getText().toString().trim();
        if (!validateVehicleId()) return;
        if(!mModelName.isEmpty()){
            if(listModel.contains(mModelName)){
                Log.e(TAG,"string contain");
                gotoFragmentInspection();
            }else AppUtils.showDialog(mActivity,getString(R.string.msg_select_valid_model_name));

        }else AppUtils.showDialog(mActivity,getString(R.string.msg_select_model_name));
    }

    private boolean validateVehicleId()    {
        mVehicleId = tie_vehicleId.getText().toString().trim();
        if (mVehicleId.isEmpty()) {
            til_vehicleId.setError(getString(R.string.msg_vehicleId_empty));
            requestFocus(tie_vehicleId);
            return false;
        }
        if(!listVehicleId.isEmpty() && listVehicleId.contains(mVehicleId)){
            til_vehicleId.setError(getString(R.string.msg_vehicle_id_found));
            requestFocus(tie_vehicleId);
            return false;
        }
        else {
            til_vehicleId.setErrorEnabled(false);
        }

        return true;
    }
    private void requestFocus(View view)    {
        if (view.requestFocus())
        {
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    public void hideKeyboard()    {
        InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tie_vehicleId.getWindowToken(), 0);
    }
    private class MyTextWatcher implements TextWatcher    {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)     {       }
        public void afterTextChanged(Editable editable)        {
            switch (view.getId()){
                case R.id.tie_vehicle_id:
                    validateVehicleId();
                    break;
            }
        }
    }

    @Override
    public void onDataReceivedSuccess(List<InspectionEntity> inspectionEntityList) {
        Log.e(TAG,"onDataReceivedSuccess");
    }

    @Override
    public void onVehicleListSuccess(List<String> vehicleList,int mode) {
        Log.e(TAG,"onVehicleListSuccess");
        if(mode == AppUtils.MODE_GETALL_USING_MODEL)   listVehicleId = vehicleList;
    }

    @Override
    public void onDataReceivedErr(String strErr) {
        Log.e(TAG,"onDataReceivedErr");
    }
    /**
     * show case code
     */
    private void initTutorials() {
        tutorials = new LinkedHashMap<>();
        tutorials.put("It's a autoComplete textview if you type model name it will suggest complete name", autoCompleteTextView);
        tutorials.put("It's a editable box to type unique vehicle id for model name, if already present in db will show u the result", tie_vehicleId);
        tutorials.put("It's a button, if you press the button take you to next screen for inspection ", btnInspect);
    }

    @Override
    public void onNext() {
        showTutorial(iterator);
    }

    @Override
    public void onComplete() {
        tutors.close();
    }

    @Override
    public void onCompleteAll() {
        tutors.close();
    }
    private void showTutorial(Iterator<Map.Entry<String, View>> iterator) {
        if (iterator == null) {
            return;
        }
        if (iterator.hasNext()) {
            Map.Entry<String, View> next = iterator.next();
            tutors.show(mActivity.getSupportFragmentManager(), next.getValue(), next.getKey(), !iterator.hasNext());
        }
    }
}
