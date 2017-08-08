package com.toroapp.toro.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;


import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.activities.MainActivity;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.Font;

import java.lang.reflect.Field;

import static com.toroapp.toro.utils.AppUtils.TAG_FORGOT_PASSWORD;


/**
 * Created by vikram on 14/7/17.
 */

public class Fragment_Login extends Fragment implements View.OnClickListener{
    private static final String  TAG = Fragment_Login.class.getSimpleName();
    private AppCompatActivity mActivity;
    private android.support.v4.app.FragmentManager mManager;
    private Bundle mSavedInstanceState;
    private Bundle mArgs;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private Font font = MyApplication.getInstance().getFontInstance();

    private Toolbar mToolbar;
    private TextInputLayout til_uname,til_password;
    private TextInputEditText tie_username,tie_password;
    private String mUsername=null,mPassword = null;
    private TextView tv_forgot_password;
    private Button btnLogin;
    private Fragment mFragment = null;
    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            mActivity = (AppCompatActivity) getActivity();
            setHasOptionsMenu(true);
            setRetainInstance(false);

            mSavedInstanceState = savedInstanceState;
            mManager = mActivity.getSupportFragmentManager();
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            mEditor= mPreferences.edit();
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)    {
        Log.e(TAG, "onCreateView");

        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        initView();
        setUpActionBar();
        setProperties();
        return rootView;
    }

    public void initView()
    {
        Log.e(TAG, "initView");
        try        {
            til_uname = (TextInputLayout) rootView.findViewById(R.id.til_username);
            til_password = (TextInputLayout) rootView.findViewById(R.id.til_password);
            tie_username = (TextInputEditText) rootView.findViewById(R.id.tie_username);
            tie_password = (TextInputEditText) rootView.findViewById(R.id.tie_password);
            tv_forgot_password = (TextView) rootView.findViewById(R.id.tv_forgot_password);
            btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(this);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void setUpActionBar()
    {
        Log.e(TAG, "setActionBar");

        try
        {
            mToolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
            mActivity.setSupportActionBar(mToolbar);
            mActivity.getSupportActionBar().setTitle(getString(R.string.lbl_login));
            mActivity.getSupportActionBar().setHomeAsUpIndicator(null);
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            TextView titleTextView = null;
            try
            {
                Field f = mToolbar.getClass().getDeclaredField("mTitleTextView");
                f.setAccessible(true);
                titleTextView = (TextView) f.get(mToolbar);
                titleTextView.setTypeface(font.getHelveticaRegular());
            }
            catch (NoSuchFieldException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogin:
                submitFormData();
                break;
            case R.id.tv_forgot_password:
                fragmentTransition(new Fragment_ForgotPassword());
                break;
        }
    }
    private void setProperties(){
        tie_username.setTypeface(font.getHelveticaRegular());
        tie_password.setTypeface(font.getHelveticaRegular());
        btnLogin.setTypeface(font.getHelveticaRegular());

        tie_username.addTextChangedListener(new MyTextWatcher(tie_username));
        tie_password.addTextChangedListener(new MyTextWatcher(tie_password));
        btnLogin.setOnClickListener(this);
        tv_forgot_password.setOnClickListener(this);
    }

    protected void submitFormData(){

        if(!validateUsername()){
            return;
        }
        if(!validatePassword())
        {
            return;
        }

        Intent intent = new Intent(mActivity, MainActivity.class);
        startActivity(intent);
        mActivity.finish();
    }

    protected void fragmentTransition( Fragment _fragment){
        this.mFragment = _fragment;
        FragmentTransaction _fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
        _fragmentTransaction.replace(R.id.frame_container_login,mFragment,TAG_FORGOT_PASSWORD);
        _fragmentTransaction.addToBackStack(TAG_FORGOT_PASSWORD);
        _fragmentTransaction.commit();
    }
    private boolean validateUsername()
    {
        /*if (!AppUtils.validateEmail(tie_username.getText().toString().trim()))
        {
            til_uname.setError(getString(R.string.msg_email_violation));
            requestFocus(tie_username);
            return false;
        }
        else */
        mUsername = tie_username.getText().toString().trim();
        if(mUsername.isEmpty()){
            til_uname.setError(getString(R.string.msg_email_empty));
            return false;
        }
        else {
            til_uname.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword()
    {
        mPassword = tie_password.getText().toString().trim();
        if (mPassword.isEmpty()) {
            til_password.setError(getString(R.string.msg_password_empty));
            requestFocus(tie_password);
            return false;
        } else {
            til_password.setErrorEnabled(false);
        }

        return true;
    }
    private void requestFocus(View view)
    {
        if (view.requestFocus())
        {
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tie_password.getWindowToken(), 0);
    }

    private class MyTextWatcher implements TextWatcher
    {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {

        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {

        }

        public void afterTextChanged(Editable editable)
        {
            switch (view.getId())
            {
                case R.id.tie_username:
                    validateUsername();
                    break;
                case R.id.tie_password:
                    validatePassword();
                    break;
            }
        }

    }

}
