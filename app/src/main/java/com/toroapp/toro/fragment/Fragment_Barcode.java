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
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.activities.BarcodeCaptureActivity;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.Font;


/**
 * Created by vikram on 15/7/17.
 */

public class Fragment_Barcode extends Fragment
{
    private static final String MODULE = Fragment_Inspection.class.getSimpleName();
    private static String TAG = "";

    private static final int RC_BARCODE_CAPTURE = 9001;
    private AppCompatActivity mActivity;
    private Toolbar mToolbar;
    private TextView text_view_title;
    private FragmentManager mManager;
    private Bundle mSavedInstanceState;
    private Bundle mArgs;
    private SharedPreferences mPreferences;
    private Font font = MyApplication.getInstance().getFontInstance();

    private TextView tv_lbl_barcode_result,tv_lbl_autofocus,tv_scan_format,tv_scan_content;
    private Button btnReadBarcode;
    private CompoundButton cb_autoFocus;
    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        TAG="onCreate";
        Log.d(MODULE,TAG);

        try
        {
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
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        TAG="onCreateView";
        Log.d(MODULE,TAG);

        try
        {
            rootView = inflater.inflate(R.layout.fragment_barcode,container,false);
            initUi();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return rootView;
    }

    private void initUi()
    {
        TAG="initUi";
        Log.d(MODULE,TAG);

        try
        {
            tv_lbl_barcode_result = (TextView) rootView.findViewById(R.id.tv_lbl_barcode_result);
            tv_scan_format = (TextView) rootView.findViewById(R.id.tv_scan_format);
            tv_scan_content = (TextView) rootView.findViewById(R.id.tv_scan_content);
            tv_lbl_autofocus = (TextView) rootView.findViewById(R.id.tv_lbl_autofocus);
            cb_autoFocus = (CompoundButton) rootView.findViewById(R.id.auto_focus);
            btnReadBarcode = (Button) rootView.findViewById(R.id.btnReadBarcode);
            btnReadBarcode.setOnClickListener(onClickListener);
            setupActionBar();
            setProperties();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    private void setProperties()
    {
        TAG="setProperties";
        Log.d(MODULE,TAG);

        try
        {
            tv_lbl_barcode_result.setTypeface(font.getRobotoRegular());
            tv_scan_format.setTypeface(font.getRobotoRegular());
            tv_scan_content.setTypeface(font.getRobotoRegular());
            btnReadBarcode.setTypeface(font.getRobotoRegular());
            tv_lbl_autofocus.setTypeface(font.getRobotoRegular());
            btnReadBarcode.setOnClickListener(onClickListener);
            setupActionBar();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }


    View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            TAG="onClick";
            Log.d(MODULE,TAG);

            if (view.getId() == R.id.btnReadBarcode)
            {
                /*int requestId = AppUtils.getIdForRequestedCamera(AppUtils.CAMERA_FACING_BACK);
                if(requestId == -1) AppUtils.showDialog(mActivity,"Camera not available");
                else
                {
                    Intent intent = new Intent(mActivity, BarcodeCaptureActivity.class);
                    intent.putExtra(BarcodeCaptureActivity.AutoFocus, cb_autoFocus.isChecked());

                    startActivityForResult(intent, RC_BARCODE_CAPTURE);
                }*/
                IntentIntegrator scanIntegrator = new IntentIntegrator(mActivity);
                scanIntegrator.initiateScan();

            }
        }
    };

    public void onStart()
    {
        super.onStart();

        TAG="onStart";
        Log.d(MODULE,TAG);

    }

    public void setupActionBar()
    {
        TAG="setupActionBar";
        Log.d(MODULE,TAG);

        try
        {
            mToolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
            text_view_title = (TextView) mActivity.findViewById(R.id.text_view_title);
            text_view_title.setText(getResources().getString(R.string.lbl_inspection));

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        TAG="onActivityResult";
        Log.d(MODULE,TAG);

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            tv_scan_format.setText(" FORMAT: " + scanFormat);
            tv_scan_content.setText(" CONTENT: " + scanContent);
        }
        else{
            Toast toast = Toast.makeText(mActivity,
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private void gotoFragmentInspection(String mModelName)
    {
        TAG="gotoFragmentInspection";
        Log.d(MODULE,TAG);

        try
        {
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
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
