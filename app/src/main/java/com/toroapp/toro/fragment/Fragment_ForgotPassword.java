package com.toroapp.toro.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.Font;

/**
 * Created by subbu on 25/11/16.
 */

public class Fragment_ForgotPassword extends Fragment {
    private static final String TAG = Fragment_ForgotPassword.class.getSimpleName();

    private AppCompatActivity mActivity;
    private Context mContext;
    private Font font = MyApplication.getInstance().getFontInstance();
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private FragmentManager mManager;

    private CoordinatorLayout cl_main;
    private TextInputLayout til_username;
    private TextInputEditText tie_username;
    private Button btn_send_email;
    private Toolbar mToolbar;
    private ImageView mImageView;
    private CollapsingToolbarLayout collapsing_toolbar;
    private Snackbar snackbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        try {
            mActivity = (AppCompatActivity) getActivity();
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            mEditor = mPreferences.edit();
            mManager = mActivity.getSupportFragmentManager();
            font = MyApplication.getInstance().getFontInstance();
            //mArgs = getArguments().getString(ARGS_BUNDLE_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");

        View rootView = null;

        try {
            rootView = (View) inflater.inflate(R.layout.fragment_forgot_password, container, false);
            initUI(rootView);
            setProperties();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return rootView;
    }

    private void initUI(View rootView) {
        Log.e(TAG, "initUI");
        try {
            cl_main = (CoordinatorLayout) mActivity.findViewById(R.id.cl_main);
            til_username = (TextInputLayout) rootView.findViewById(R.id.til_username);

            tie_username = (TextInputEditText) rootView.findViewById(R.id.tie_username);

            btn_send_email = (Button) rootView.findViewById(R.id.btn_send_email);
            //setupActionBar();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setupActionBar() {
        mToolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.lbl_forget_password));
        mActivity.setSupportActionBar(mToolbar);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });
    }

    private void setProperties() {
        Log.e(TAG, "setProperties");

        til_username.setTypeface(font.getRobotoRegular());
        tie_username.setTypeface(font.getRobotoRegular());
        btn_send_email.setTypeface(font.getRobotoRegular());

        btn_send_email.setOnClickListener(_OnClickListener);
        tie_username.addTextChangedListener(new MyTextWatcher(tie_username));
    }

    View.OnClickListener _OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e(TAG, "onClick");
            hideKeyboard();
            switch (v.getId()) {
                case R.id.btn_send_email:
                    submitForm();
                    break;
                default:
                    break;
            }
        }
    };

    private void submitForm() {
        Log.e(TAG, "submitForm");
        try {
            hideKeyboard();

            if (!validateUsername()) {
                return;
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private boolean validateUsername() {
        if (tie_username.getText().toString().trim().isEmpty()) {
            til_username.setError(getString(R.string.msg_username_empty));
            requestFocus(tie_username);
            return false;
        } else {
            til_username.setErrorEnabled(false);
        }

        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tie_username.getWindowToken(), 0);
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.tie_username:
                    validateUsername();
                    break;

            }
        }

    }

}
