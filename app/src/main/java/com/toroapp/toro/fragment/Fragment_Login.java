package com.toroapp.toro.fragment;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.michael.easydialog.EasyDialog;
import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.activities.MainActivity;
import com.toroapp.toro.localstorage.dbhelper.InspectionDbInitializer;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.Font;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import static com.toroapp.toro.utils.AppUtils.TAG_FORGOT_PASSWORD;


/**
 * Created by vikram on 14/7/17.
 */

public class Fragment_Login extends Fragment implements View.OnClickListener
{
    private static final String MODULE = Fragment.class.getSimpleName();
    private static String TAG = "";

    private AppCompatActivity mActivity;
    private android.support.v4.app.FragmentManager mManager;
    private Bundle mSavedInstanceState;
    private Bundle mArgs;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private Font font = MyApplication.getInstance().getFontInstance();

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private Toolbar mToolbar;
    private TextInputLayout til_uname,til_password;
    private AppCompatEditText tie_username,tie_password;
    private String mUsername=null,mPassword = null;
    private TextView tv_forgot_password;
    private Button btnLogin;
    private Fragment mFragment = null;
    private View rootView,infoView;
    private Map<String, View> tutorials;
    private Iterator<Map.Entry<String, View>> iterator;
    private InspectionDbInitializer inspectionDbInitializer;
    private Intent recognizerIntent;
    private ImageView info_view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)    {
        TAG="onCreateView";
        Log.d(MODULE,TAG);

        rootView = inflater.inflate(R.layout.fragment_login, container, false);

        infoView = inflater.inflate(R.layout.info_view_login, container, false);
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
        try        {
            til_uname = (TextInputLayout) rootView.findViewById(R.id.til_username);
            til_password = (TextInputLayout) rootView.findViewById(R.id.til_password);
            tie_username = (AppCompatEditText) rootView.findViewById(R.id.tie_username);
            tie_password = (AppCompatEditText) rootView.findViewById(R.id.tie_password);
            tv_forgot_password = (TextView) rootView.findViewById(R.id.tv_forgot_password);
            info_view = (ImageView) rootView.findViewById(R.id.info_view);
            btnLogin = (Button) rootView.findViewById(R.id.btnLogin);

            btnLogin.setOnClickListener(this);
            info_view.setOnClickListener(this);
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
            case R.id.info_view:
                showInfoView();
                break;
        }
    }

    private void showInfoView() {
        try {
            if(infoView.getParent()!=null)
                ((ViewGroup)infoView.getParent()).removeView(infoView); // <- fix

            new EasyDialog(mActivity)
                    // .setLayoutResourceId(R.layout.layout_tip_content_horizontal)//layout resource id
                    .setLayout(infoView)
                    .setBackgroundColor(mActivity.getResources().getColor(R.color.calendarBackground))
                    // .setLocation(new location[])//point in screen
                    .setLocationByAttachedView(info_view)
                    .setGravity(EasyDialog.GRAVITY_TOP)
                    .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 1000, -600, 100, -50, 50, 0)
                    .setAnimationAlphaShow(1000, 0.3f, 1.0f)
                    .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 500, -50, 800)
                    .setAnimationAlphaDismiss(500, 1.0f, 0.0f)
                    .setTouchOutsideDismiss(true)
                    .setMatchParent(true)
                    .setMarginLeftAndRight(24, 24)
                    .setOutsideColor(mActivity.getResources().getColor(R.color.transparent))
                    .show();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void setProperties(){
        tie_username.setTypeface(font.getRobotoRegular());
        tie_password.setTypeface(font.getRobotoRegular());
        btnLogin.setTypeface(font.getRobotoRegular());

        tie_username.addTextChangedListener(new MyTextWatcher(tie_username));
        tie_password.addTextChangedListener(new MyTextWatcher(tie_password));
        btnLogin.setOnClickListener(this);
        tv_forgot_password.setOnClickListener(this);

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //Fragment_Login.this.tv_forgot_password.setAlpha((Float) animation.getAnimatedValue());
            }
        });

        animator.setDuration(500);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(-1);
        animator.start();
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
            til_uname.setError(getString(R.string.msg_enter_your_username));
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


    @Override
    public void onPause() {
        super.onPause();

        TAG="onPause";
        Log.d(MODULE,TAG);
    }
}
