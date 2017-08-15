package com.toroapp.toro.fragment;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
import com.popalay.tutors.TutorialListener;
import com.popalay.tutors.Tutors;
import com.popalay.tutors.TutorsBuilder;
import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.activities.MainActivity;
import com.toroapp.toro.listeners.InspectionDataListener;
import com.toroapp.toro.localstorage.dbhelper.InspectionDbInitializer;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.Font;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.toroapp.toro.utils.AppUtils.ALL_PERMISSIONS_RESULT;
import static com.toroapp.toro.utils.AppUtils.TAG_FORGOT_PASSWORD;


/**
 * Created by vikram on 14/7/17.
 */

public class Fragment_Login extends Fragment implements View.OnClickListener,TutorialListener,RecognitionListener
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
    private View rootView;
    private Map<String, View> tutorials;
    private Iterator<Map.Entry<String, View>> iterator;
    private Tutors tutors;
    private InspectionDbInitializer inspectionDbInitializer;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

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
            permissions.add(RECORD_AUDIO);
            permissionsToRequest = findUnAskedPermissions(permissions);
            //get the permissions we have asked for before but are not granted..
            //we will store this in a global list to access later.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0)
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }


            speech = SpeechRecognizer.createSpeechRecognizer(mActivity);
            speech.setRecognitionListener(this);
            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                    "en");
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                    mActivity.getPackageName());
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);


        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)    {
        TAG="onCreateView";
        Log.d(MODULE,TAG);

        rootView = inflater.inflate(R.layout.fragment_login, container, false);
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
            btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(this);
            speech.cancel();
            speech.startListening(recognizerIntent);
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

        tutors = new TutorsBuilder()
                .textColorRes(android.R.color.white)
                .shadowColorRes(R.color.shadow)
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
/**
 * show case code
 */
private void initTutorials() {
    tutorials = new LinkedHashMap<>();
    tutorials.put("It's a toolbar", tie_username);
    tutorials.put("It's a button", tie_password);
    tutorials.put("It's a borderless button", btnLogin);
    tutorials.put("It's a text", tv_forgot_password);
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

    @Override
    public void onPause() {
        super.onPause();

        TAG="onPause";
        Log.d(MODULE,TAG);

        if (speech != null) {
            speech.destroy();
            Log.d(MODULE,TAG);
        }

    }

    @Override
    public void onBeginningOfSpeech() {
        TAG="onBeginningOfSpeech";
        Log.d(MODULE,TAG);

    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        TAG="onBufferReceived";
        Log.d(MODULE,TAG);

    }

    @Override
    public void onEndOfSpeech() {
        TAG="onEndOfSpeech";
        Log.d(MODULE,TAG);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        TAG="onError";
        Log.d(MODULE,TAG);
        Log.e(MODULE,TAG + errorMessage);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        TAG="onEvent";
        Log.d(MODULE,TAG);
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        TAG="onPartialResults";
        Log.d(MODULE,TAG);
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        TAG="onReadyForSpeech";
        Log.d(MODULE,TAG);
    }

    @Override
    public void onResults(Bundle results) {
        TAG="onResults";
        Log.d(MODULE,TAG);
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        tie_username.setText(text);

    }

    @Override
    public void onRmsChanged(float rmsdB) {
        TAG="onRmsChanged";
        Log.d(MODULE,TAG);
    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                speech.destroy();
                speech.startListening(recognizerIntent);
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (mActivity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //MainActivityPermissionsDispatcher.onRequestPermissionsResult(mActivity, requestCode, grantResults);
        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (hasPermission(perms)) {

                    } else {

                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                                //Log.d("API123", "permisionrejected " + permissionsRejected.size());

                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }
    }

}
