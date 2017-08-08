package com.toroapp.toro.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.soundcloud.android.crop.Crop;
import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.listeners.InspectionDataListener;
import com.toroapp.toro.localstorage.database.AppDatabase;
import com.toroapp.toro.localstorage.dbhelper.InspectionDbInitializer;
import com.toroapp.toro.localstorage.entity.InspectionEntity;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.Font;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by subbu on 25/11/16.
 */

public class Fragment_Inspection3 extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener,
        InspectionDataListener{
    private static final String TAG = Fragment_Inspection3.class.getSimpleName();

    private AppCompatActivity mActivity;
    private Context mContext;
    private Font font = MyApplication.getInstance().getFontInstance();
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private FragmentManager mManager;
    private Handler mHandler;

    private TextView tv_model_name, tv_lbl_basic_check, tv_lbl_quesno, tv_lbl_question;
    private RadioGroup radioGroup;
    private RadioButton rdb_yes, rdb_no;
    private EditText et_remarks;
    private Button btn_capture_photo,btnNext;
    private ImageView iv_captured_image;
    private Uri mImageCaptureUri;
    private String mStringJson = null;

    Bitmap myBitmap;
    Uri picUri;
    private String mUniqueKey=null,mModelName=null,mInspectionName=null,mRemarks =null,mTested=null,mImageData = null;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 107;
    private static final String AUTHORITY="com.toroapp.toro";
    private CoordinatorLayout cl_main;
    private Toolbar mToolbar;
    private Bundle mArgs;
    View rootView = null;
    private Snackbar snackbar;
    //local db
    private InspectionDbInitializer mInspectionDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        try {
            mActivity = (AppCompatActivity) getActivity();
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            mEditor = mPreferences.edit();
            mHandler = new Handler();
            mStringJson = mPreferences.getString(AppUtils.SHARED_LOGIN, null);
            mManager = mActivity.getSupportFragmentManager();
            font = MyApplication.getInstance().getFontInstance();
            mArgs = getArguments();
            mInspectionDb = new InspectionDbInitializer(this);
            permissions.add(CAMERA);
            permissions.add(READ_EXTERNAL_STORAGE);
            permissions.add(WRITE_EXTERNAL_STORAGE);
            permissionsToRequest = findUnAskedPermissions(permissions);
            //get the permissions we have asked for before but are not granted..
            //we will store this in a global list to access later.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0)
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        try {
            rootView = (View) inflater.inflate(R.layout.fragment_inspect3, container, false);
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
            tv_model_name = (TextView) rootView.findViewById(R.id.tv_lbl_title_model_name);
            tv_lbl_basic_check = (TextView) rootView.findViewById(R.id.tv_lbl_basic_check);
            tv_lbl_quesno = (TextView) rootView.findViewById(R.id.tv_lbl_quesno);
            tv_lbl_question = (TextView) rootView.findViewById(R.id.tv_lbl_question);
            radioGroup = (RadioGroup) rootView.findViewById(R.id.radiogroup);
            rdb_yes = (RadioButton) rootView.findViewById(R.id.rdb_yes);
            rdb_no = (RadioButton) rootView.findViewById(R.id.rdb_no);
            et_remarks = (EditText) rootView.findViewById(R.id.et_remarks);
            btn_capture_photo = (Button) rootView.findViewById(R.id.btn_capture_photo);
            btnNext = (Button) rootView.findViewById(R.id.btn_next);
            iv_captured_image = (ImageView) rootView.findViewById(R.id.iv_captured_image);

            setupActionBar();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setupActionBar() {
        mToolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.lbl_inspection));
        mActivity.setSupportActionBar(mToolbar);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void setProperties() {
        Log.e(TAG, "setProperties");
        iv_captured_image.setVisibility(View.GONE);
        tv_model_name.setTypeface(font.getHelveticaRegular());
        tv_lbl_basic_check.setTypeface(font.getHelveticaRegular());
        tv_lbl_quesno.setTypeface(font.getHelveticaRegular());
        tv_lbl_question.setTypeface(font.getHelveticaRegular());
        rdb_yes.setTypeface(font.getHelveticaRegular());
        rdb_no.setTypeface(font.getHelveticaRegular());
        et_remarks.setTypeface(font.getHelveticaRegular());
        btn_capture_photo.setTypeface(font.getHelveticaRegular());

        radioGroup.setOnCheckedChangeListener(this);
        btn_capture_photo.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        if(mArgs!=null){
            if(mArgs.containsKey(AppUtils.ARGS_MODEL)){
                mModelName = mArgs.getString(AppUtils.ARGS_MODEL);
                mInspectionName = getString(R.string.lbl_ques_3);
                mInspectionName = mInspectionName.substring(3);
                mUniqueKey = mInspectionName + mModelName;
                Log.e(TAG,"Substring ins name :"+ mInspectionName);
                tv_model_name.setText(mModelName);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_capture_photo:
                //ShowSelectPhotoOption();
                break;
            case R.id.btn_next:
                submitData();
                break;
        }
    }

    private void submitData() {
        Log.e(TAG,"submitData");
        if(mModelName!=null){
            if(mInspectionName!=null){
                if(mTested!=null){
                    InspectionEntity inspectionEntity = new InspectionEntity(mUniqueKey,mInspectionName,mModelName,mTested,mRemarks,mImageData);
                    mInspectionDb.insertSingleData(AppDatabase.getAppDatabase(mActivity),inspectionEntity,AppUtils.MODE_INSERT);
                    gotoFragmentFinal();
                }else AppUtils.showDialog(mActivity,"Kindly choose yes or no");
            }else AppUtils.showDialog(mActivity,"Inspection name is empty");
        }else AppUtils.showDialog(mActivity,"Model name is empty");
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
        RadioButton rdb = (RadioButton) rootView.findViewById(checkedId);
        mTested = rdb.getText().toString();
        Log.e(TAG, "checked ::" + mTested);
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

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

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
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /*vikram code*/
    private void ShowSelectPhotoOption()    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.lbl_select_photo)
                .setItems(R.array.array_photo_option, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //dispatchTakePictureIntent();
                                break;
                            case 1:
                                Log.e(TAG,"option b");
                                Intent intent = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, 2);
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                });
        builder.show();

    }
    private void dispatchTakePictureIntent()    {
        Log.d(TAG,"dispatchTakePictureIntent");

        String path= mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"";
        //File file = new File("IMG_"+System.currentTimeMillis()+".jpg");
        //file.getParentFile().mkdirs();
        File file = new File(mActivity.getFilesDir(),"test.jpg");
        try {
            file.createNewFile();
        }catch (IOException e) {
            e.printStackTrace();
        }
        mImageCaptureUri = FileProvider.getUriForFile(mActivity, AUTHORITY, file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, 101);
    }
    private void beginCrop(Uri source)
    {
        //String Str_ImagePath ="file://" + AppUtils.getProfilePicturePath(mActivity) + "/" + mUser.getMobile_Number() + ".png";
        String Str_ImagePath = AppUtils.getProfilePicturePath(mActivity) + "/" + "ques1" + ".png";
        Uri destination = Uri.fromFile(new File(Str_ImagePath));
        Crop.of(source, destination).withMaxSize(640,420).start(mActivity, this, 10);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d(TAG,"onActivityResult" + " requestCode ::: " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==mActivity.RESULT_OK){

        if (requestCode == 101 && resultCode==mActivity.RESULT_OK)
        {
            beginCrop(mImageCaptureUri);
        }
        else if (requestCode == 10)
        {
            handleCrop(resultCode, data);
        }
        else if(requestCode==2){
            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = mActivity.getContentResolver().query(selectedImage,filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            Log.w("path of image from gallery......******************.........", picturePath+"");
            iv_captured_image.setImageBitmap(thumbnail);
        }
        }
    }
    private void handleCrop(int resultCode, Intent result)    {
        if (resultCode == getActivity().RESULT_OK)        {
            try            {
                iv_captured_image.setImageDrawable(null);

            }
            catch (Exception e)            {
                e.printStackTrace();
            }

        }
        else if (resultCode == Crop.RESULT_ERROR)        {
            Log.d(TAG,"result error " + ":::" + Crop.getError(result).getMessage());
        }
    }


    @Override
    public void onDataReceivedSuccess(List<InspectionEntity> inspectionEntityList) {
    Log.e(TAG,"onDataReceivedSuccess");
    }

    @Override
    public void onDataReceivedErr(String strErr) {
        Log.e(TAG,"onDataReceivedErr");
        AppUtils.showDialog(mActivity,strErr);
    }
    private void gotoFragmentFinal() {
        Fragment fragment = new Fragment_Final();
        Bundle data = new Bundle();
        data.putString(AppUtils.ARGS_MODEL,mModelName);
        fragment.setArguments(data);
        FragmentTransaction fragmentTransaction = mManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame_container, fragment,AppUtils.TAG_FINAL);
        fragmentTransaction.addToBackStack(AppUtils.TAG_FINAL);
        fragmentTransaction.commit();
    }
}
