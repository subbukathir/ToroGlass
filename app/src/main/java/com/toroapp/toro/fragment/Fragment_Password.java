package com.toroapp.toro.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.activities.MainActivity;
import com.toroapp.toro.listeners.VoiceListener;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.Font;

import static com.toroapp.toro.utils.AppUtils.TAG_FORGOT_PASSWORD;


/**
 * Created by vikram on 14/7/17.
 */

public class Fragment_Password extends Fragment implements VoiceListener
{
    private static final String MODULE = Fragment_Password.class.getSimpleName();
    private static String TAG = "";

    private AppCompatActivity mActivity;
    private android.support.v4.app.FragmentManager mManager;
    private Bundle mSavedInstanceState;
    private Bundle mArgs;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private Font font = MyApplication.getInstance().getFontInstance();

    private Toolbar mToolbar;
    private AppCompatEditText et_password;
    private TextInputLayout til_password;
    private TextView text_view_title;
    private AppCompatButton btnLogin;
    private String mPassword =null;
    private Fragment mFragment = null;
    private View rootView;
    private Fragment_Voice_Recognition fragmentVoiceRecognition;
    private FrameLayout frame_layout_voice;

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
            mEditor= mPreferences.edit();
            mArgs = getArguments();
            if (mActivity.getCurrentFocus() != null)
            {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
            }
            fragmentVoiceRecognition = new Fragment_Voice_Recognition();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        TAG="onCreateView";
        Log.d(MODULE,TAG);

        rootView = inflater.inflate(R.layout.fragment_password, container, false);
        initView();
        //setUpActionBar();
        //initTutorials();
        setProperties();
        return rootView;
    }

    public void initView()
    {
        TAG="initView";
        Log.d(MODULE,TAG);
        try
        {
            setUpActionBar();
            frame_layout_voice = (FrameLayout) rootView.findViewById(R.id.frame_layout_voice);
            til_password = (TextInputLayout) rootView.findViewById(R.id.til_password);
            et_password = (AppCompatEditText) rootView.findViewById(R.id.et_password);
            btnLogin = (AppCompatButton) rootView.findViewById(R.id.btnLogin);
            setProperties();
            if(frame_layout_voice.getChildCount()==0)
                addVoiceRecognition(fragmentVoiceRecognition);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void setUpActionBar()
    {
        TAG="setUpActionBar";
        Log.d(MODULE,TAG);

        try
        {
            mToolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
            text_view_title = (TextView) mActivity.findViewById(R.id.text_view_title);
            text_view_title.setTypeface(font.getRobotoRegular());
            text_view_title.setText(getString(R.string.lbl_login));
            mActivity.setSupportActionBar(mToolbar);
            mActivity.getSupportActionBar().setHomeAsUpIndicator(null);
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
            et_password.setTypeface(font.getRobotoRegular());
            btnLogin.setTypeface(font.getRobotoRegular());

            btnLogin.setOnClickListener(_OnClickListener);
            et_password.addTextChangedListener(new MyTextWatcher(et_password));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    protected void submitFormData()
    {

        TAG="submitFormData";
        Log.d(MODULE,TAG);

        try
        {
            if(!validatePassword())
            {
                return;
            }
            Intent intent = new Intent(mActivity, MainActivity.class);
            startActivity(intent);
            mActivity.finish();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    protected void addVoiceRecognition(final Fragment_Voice_Recognition fragment)
    {
        TAG="addVoiceRecognition";
        Log.d(MODULE,TAG);

        try
        {
            fragment.setVoiceListener(this);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout_voice,fragment,Fragment_Voice_Recognition.class.getName());
            transaction.commit();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    //Do something after 100ms
                    fragment.startListening(getString(R.string.lbl_say_password));
                }
            }, 3000);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    protected void fragmentTransition( Fragment _fragment)
    {
        TAG="fragmentTransition";
        Log.d(MODULE,TAG);
        try
        {
            this.mFragment = _fragment;
            FragmentTransaction _fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
            _fragmentTransaction.replace(R.id.frame_container_login,mFragment,TAG_FORGOT_PASSWORD);
            _fragmentTransaction.addToBackStack(TAG_FORGOT_PASSWORD);
            _fragmentTransaction.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private boolean validatePassword()
    {
        TAG="submitFormData";
        Log.d(MODULE,TAG);
        try
        {
            /*if (!AppUtils.validateEmail(tie_username.getText().toString().trim()))
            {
                til_uname.setError(getString(R.string.msg_email_violation));
                requestFocus(tie_username);
                return false;
            }
            else */
            mPassword = et_password.getText().toString().trim();
            if(mPassword.isEmpty())
            {
                til_password.setError(getString(R.string.msg_enter_your_password));
                return false;
            }
            else
            {
                til_password.setErrorEnabled(false);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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
        imm.hideSoftInputFromWindow(et_password.getWindowToken(), 0);
    }

    View.OnClickListener _OnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {

            switch (view.getId())
            {
                case R.id.btnLogin:
                     submitFormData();
                     break;
                case R.id.tv_forgot_password:
                     fragmentTransition(new Fragment_ForgotPassword());
                     break;
            }
        }
    };

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
                case R.id.tie_password:
                    validatePassword();
                    break;
            }
        }

    }

    @Override
    public void onVoiceEnd(boolean success, String text)
    {
        TAG="onVoiceEnd";
        Log.d(MODULE,TAG);

        try
        {
            if(success) et_password.setText(text);
            else Log.d(MODULE,TAG + " Text : " + text);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
