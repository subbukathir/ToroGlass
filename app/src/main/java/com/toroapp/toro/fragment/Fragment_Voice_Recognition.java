package com.toroapp.toro.fragment;

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
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.listeners.VoiceListener;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.Font;

import java.util.ArrayList;

import static android.Manifest.permission.RECORD_AUDIO;
import static com.toroapp.toro.utils.AppUtils.ALL_PERMISSIONS_RESULT;


/**
 * Created by vikram on 14/7/17.
 */

public class Fragment_Voice_Recognition extends Fragment implements RecognitionListener {
    private static final String MODULE = Fragment_Voice_Recognition.class.getName();
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


    private AppCompatTextView text_view_search_title;
    private ProgressBar progressBar;
    private Fragment mFragment = null;
    private View rootView;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private VoiceListener voiceListener;

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


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TAG = "onCreateView";
        Log.d(MODULE, TAG);

        rootView = inflater.inflate(R.layout.fragment_voice_recognition, container, false);
        initView();
        setProperties();
        return rootView;
    }

    public void initView() {
        TAG = "initView";
        Log.d(MODULE, TAG);
        try {
            text_view_search_title = (AppCompatTextView) rootView.findViewById(R.id.text_view_search_title);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setProperties() {
        TAG = "setProperties";
        Log.d(MODULE, TAG);
        try {
            text_view_search_title.setTypeface(font.getRobotoRegular());
            progressBar.setVisibility(View.GONE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void setVoiceListener(VoiceListener voiceListener) {
        this.voiceListener = voiceListener;
    }

    public void startListening(String text) {
        TAG = "startListening";
        Log.d(MODULE, TAG);

        try {
            if (speech != null) {
                if (mActivity != null && isAdded()) {
                    text_view_search_title.setText(text);
                    progressBar.setVisibility(View.VISIBLE);
                    speech.startListening(recognizerIntent);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        TAG = "onPause";
        Log.d(MODULE, TAG);

        if (speech != null) {
            speech.destroy();
            Log.d(MODULE, TAG);
        }

    }

    @Override
    public void onBeginningOfSpeech() {
        TAG = "onBeginningOfSpeech";
        Log.d(MODULE, TAG);

    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        TAG = "onBufferReceived";
        Log.d(MODULE, TAG);
    }

    @Override
    public void onEndOfSpeech() {
        TAG = "onEndOfSpeech";
        Log.d(MODULE, TAG);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        TAG = "onError";
        Log.d(MODULE, TAG);
        Log.e(MODULE, TAG + errorMessage);
        progressBar.setVisibility(View.GONE);
        voiceListener.onVoiceEnd(false, errorMessage);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        TAG = "onEvent";
        Log.d(MODULE, TAG);
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        TAG = "onPartialResults";
        Log.d(MODULE, TAG);
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        TAG = "onReadyForSpeech";
        Log.d(MODULE, TAG);
    }

    @Override
    public void onResults(Bundle results) {
        TAG = "onResults";
        Log.d(MODULE, TAG);
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        text = matches.get(0);
        voiceListener.onVoiceEnd(true, text);
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        TAG = "onRmsChanged";
        Log.d(MODULE, TAG);
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
                            showMessageOKCancel("These permissions are mandatory for the applicatio9n. Please allow access.",
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