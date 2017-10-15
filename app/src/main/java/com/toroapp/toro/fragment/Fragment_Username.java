package com.toroapp.toro.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.toroapp.toro.listeners.VoiceListener;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.Font;
import com.vuzix.speech.VoiceControl;

import static com.toroapp.toro.utils.AppUtils.TAG_FORGOT_PASSWORD;


/**
 * Created by vikram on 14/7/17.
 */

public class Fragment_Username extends Fragment implements VoiceListener {
    private static final String MODULE = Fragment_Username.class.getSimpleName();
    private static String TAG = "";

    private AppCompatActivity mActivity;
    private android.support.v4.app.FragmentManager mManager;
    private Bundle mSavedInstanceState;
    private Bundle mArgs;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private Font font = MyApplication.getInstance().getFontInstance();

    private Toolbar mToolbar;
    private AppCompatEditText et_username;
    private TextInputLayout til_username;
    private TextView text_view_title;
    private AppCompatButton btnNext;
    private String mUsername = null;
    private Fragment mFragment = null;
    private View rootView;
    private Fragment_Voice_Recognition fragmentVoiceRecognition;
    private FrameLayout frame_layout_voice;
    private CountDownTimer timer;
    private VoiceControl vc;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = "onCreate";
        Log.d(MODULE, TAG);

        try {
            mActivity = (AppCompatActivity) getActivity();
            setHasOptionsMenu(true);
            setRetainInstance(false);

            mSavedInstanceState = savedInstanceState;
            mManager = mActivity.getSupportFragmentManager();
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            mEditor = mPreferences.edit();
            mArgs = getArguments();
            if (mActivity.getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
            }
            fragmentVoiceRecognition = new Fragment_Voice_Recognition();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TAG = "onCreateView";
        Log.d(MODULE, TAG);

        rootView = inflater.inflate(R.layout.fragment_username, container, false);
        initView();
        //setUpActionBar();
        //initTutorials();
        setProperties();
        return rootView;
    }

    public void initView() {
        TAG = "initView";
        Log.d(MODULE, TAG);
        try {
            setUpActionBar();
            frame_layout_voice = (FrameLayout) rootView.findViewById(R.id.frame_layout_voice);
            til_username = (TextInputLayout) rootView.findViewById(R.id.til_username);
            et_username = (AppCompatEditText) rootView.findViewById(R.id.et_username);
            btnNext = (AppCompatButton) rootView.findViewById(R.id.btnNext);
            setProperties();
            if (frame_layout_voice.getChildCount() == 0)
                addVoiceRecognition(fragmentVoiceRecognition);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setUpActionBar() {
        TAG = "setUpActionBar";
        Log.d(MODULE, TAG);

        try {
            mToolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
            text_view_title = (TextView) mActivity.findViewById(R.id.text_view_title);
            text_view_title.setTypeface(font.getRobotoRegular());
            text_view_title.setText(getString(R.string.lbl_login));
            mActivity.setSupportActionBar(mToolbar);
            mActivity.getSupportActionBar().setHomeAsUpIndicator(null);
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setProperties() {
        TAG = "setProperties";
        Log.d(MODULE, TAG);

        try {
            et_username.setTypeface(font.getRobotoRegular());
            btnNext.setTypeface(font.getRobotoRegular());

            btnNext.setOnClickListener(_OnClickListener);
            et_username.addTextChangedListener(new MyTextWatcher(et_username));

            // Create the VoiceControl object and pass it the context.
            vc = new VoiceControl(mActivity) {
                @Override
                protected void onRecognition(String word) {
                    // Set the TextView to contain whatever word the recognition
                    // service picks up. It is important that the View is cast to
                    // a TextView via parentheses before setText is called.
                    et_username.setText(word);
                }
            };
            // Basic grammar is included by default.
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void submitFormData() {

        TAG = "submitFormData";
        Log.d(MODULE, TAG);

        try {
            if (!validateUsername()) {
                return;
            }
            fragmentTransition(new Fragment_Password());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void addVoiceRecognition(final Fragment_Voice_Recognition fragment) {
        TAG = "addVoiceRecognition";
        Log.d(MODULE, TAG);

        try {
            fragment.setVoiceListener(this);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout_voice, fragment, Fragment_Voice_Recognition.class.getName());
            transaction.commit();
            /*final Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    if(isAdded() && mActivity !=null)
                    //Do something after 100ms
                    fragment.startListening(getString(R.string.lbl_say_user_name));
                }
            }, 3000);*/
            //startTimer.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void fragmentTransition(Fragment _fragment) {
        TAG = "submitFormData";
        Log.d(MODULE, TAG);

        try {
            this.mFragment = _fragment;
            Bundle data = new Bundle();
            data.putString(AppUtils.ARGS_USERNAME, mUsername);
            mFragment.setArguments(data);
            FragmentTransaction _fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
            _fragmentTransaction.replace(R.id.frame_container_login, mFragment, TAG_FORGOT_PASSWORD);
            _fragmentTransaction.addToBackStack(TAG_FORGOT_PASSWORD);
            _fragmentTransaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean validateUsername() {

        TAG = "validateUsername";
        Log.d(MODULE, TAG);

        try {
            /*if (!AppUtils.validateEmail(tie_username.getText().toString().trim()))
            {
                til_uname.setError(getString(R.string.msg_email_violation));
                requestFocus(tie_username);
                return false;
            }
            else */
            mUsername = et_username.getText().toString().trim();
            if (mUsername.isEmpty()) {
                til_username.setError(getString(R.string.msg_enter_your_username));
                return false;
            } else {
                til_username.setErrorEnabled(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
        imm.hideSoftInputFromWindow(et_username.getWindowToken(), 0);
    }

    View.OnClickListener _OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btnNext:
                    submitFormData();
                    break;
                case R.id.tv_forgot_password:
                    fragmentTransition(new Fragment_ForgotPassword());
                    break;
            }
        }
    };

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

    @Override
    public void onVoiceEnd(boolean success, String text) {
        TAG = "onVoiceEnd";
        Log.d(MODULE, TAG);

        try {
            Log.d(MODULE, TAG + " Text : " + text);
            if (success) {
                et_username.setText(text);
                startTimer.cancel();
                endTimer.cancel();
            } else {
                endTimer.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    CountDownTimer startTimer = new CountDownTimer(10000, 1000) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            endTimer.start();
        }
    };

    CountDownTimer endTimer = new CountDownTimer(5000, 1000) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            startTimer.start();

        }
    };

}
