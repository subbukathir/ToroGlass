package com.toroapp.toro.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.activities.BarcodeCaptureActivity;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.Font;


/**
 * Created by vikram on 15/7/17.
 */

public class Fragment_Barcode extends Fragment {
    private static final String TAG = Fragment_Barcode.class.getSimpleName();
    private static final int RC_BARCODE_CAPTURE = 9001;
    private AppCompatActivity mActivity;
    private Toolbar mToolbar;
    private FragmentManager mManager;
    private Bundle mSavedInstanceState;
    private Bundle mArgs;
    private SharedPreferences mPreferences;
    private Font font = MyApplication.getInstance().getFontInstance();

    private TextView textView_barcode_result;
    private Button btnReadBarcode;
    private CompoundButton cb_autoFocus;
    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mActivity = (AppCompatActivity) getActivity();
            setHasOptionsMenu(true);
            setRetainInstance(false);

            mSavedInstanceState = savedInstanceState;
            mManager = mActivity.getSupportFragmentManager();
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            mArgs = getArguments();
            if (mActivity.getCurrentFocus() != null)
            {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_barcode,container,false);
        initUi();
        return rootView;
    }

    private void initUi() {
        textView_barcode_result = (TextView) rootView.findViewById(R.id.txtview_barcode_result);
        cb_autoFocus = (CompoundButton) rootView.findViewById(R.id.auto_focus);
        btnReadBarcode = (Button) rootView.findViewById(R.id.btnReadBarcode);
        btnReadBarcode.setOnClickListener(onClickListener);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId()==R.id.btnReadBarcode){
                if (view.getId() == R.id.btnReadBarcode){
                    int requestId = AppUtils.getIdForRequestedCamera(AppUtils.CAMERA_FACING_BACK);
                    if(requestId == -1) AppUtils.showDialog(mActivity,"Camera not available");
                    else {
                        Intent intent = new Intent(mActivity, BarcodeCaptureActivity.class);
                        intent.putExtra(BarcodeCaptureActivity.AutoFocus, cb_autoFocus.isChecked());

                        startActivityForResult(intent, RC_BARCODE_CAPTURE);
                    }

                }
            }
        }
    };
    public void onStart() {
        super.onStart();
        setupActionbar();
    }

    private void setupActionbar(){
        Log.e(TAG,"setupActionbar");

        mToolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
        mActivity.setSupportActionBar(mToolbar);
        mActivity.getSupportActionBar().setTitle(getString(R.string.lbl_barcode_inspection));
        mActivity.getSupportActionBar().setHomeAsUpIndicator(null);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG,"onActivityResult");
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    textView_barcode_result.setText(getString(R.string.lbl_success) +" : "+barcode.displayValue);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    gotoFragmentInspection(barcode.displayValue);
                } else {
                    textView_barcode_result.setText(R.string.lbl_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                textView_barcode_result.setText(getString(R.string.lbl_barcode_error)+" : "+CommonStatusCodes.getStatusCodeString(resultCode));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void gotoFragmentInspection(String mModelName) {
        Fragment fragment = new Fragment_Inspection();
        Bundle data = new Bundle();
        data.putString(AppUtils.ARGS_MODEL,mModelName);
        fragment.setArguments(data);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame_container, fragment,AppUtils.TAG_FRAGMENT_INSPECTION);
        fragmentTransaction.addToBackStack(AppUtils.TAG_FRAGMENT_INSPECTION);
        fragmentTransaction.commit();
    }
}
