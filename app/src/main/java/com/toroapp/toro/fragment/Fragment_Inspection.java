package com.toroapp.toro.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
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

import com.toroapp.toro.BuildConfig;
import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.listeners.InspectionDataListener;
import com.toroapp.toro.localstorage.database.AppDatabase;
import com.toroapp.toro.localstorage.dbhelper.InspectionDbInitializer;
import com.toroapp.toro.localstorage.entity.InspectionEntity;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.Font;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.toroapp.toro.utils.AppUtils.ALL_PERMISSIONS_RESULT;
import static com.toroapp.toro.utils.AppUtils.REQUEST_PICK_PHOTO;
import static com.toroapp.toro.utils.AppUtils.REQUEST_TAKE_PHOTO;

/**
 * Created by subbu on 25/11/16.
 */

public class  Fragment_Inspection extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener,
        InspectionDataListener{
    private static final String TAG = Fragment_Inspection.class.getSimpleName();

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

    private String mUniqueKey=null, mModelName=null,mInspectionName=null,mRemarks =null,mTested=null,mImageData = null;
    private String mCurrentPhotoPath;
    public static int mRequest = 0;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

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
            setRetainInstance(true);
            mPreferences = mActivity.getSharedPreferences(AppUtils.SHARED_PREFS, Context.MODE_PRIVATE);
            mEditor = mPreferences.edit();
            mHandler = new Handler();
            mStringJson = mPreferences.getString(AppUtils.SHARED_LOGIN, null);
            mManager = mActivity.getSupportFragmentManager();
            font = MyApplication.getInstance().getFontInstance();
            mArgs = getArguments();
            mInspectionDb = new InspectionDbInitializer(this);
            //mInspectionDb.getInspectionData(AppDatabase.getAppDatabase(mActivity),AppUtils.MODE_DELETE_ALL);
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
            rootView = (View) inflater.inflate(R.layout.fragment_inspect1, container, false);
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
                mInspectionName = getString(R.string.lbl_ques_1);
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
                ShowSelectPhotoOption();
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
                    gotoFragmentInspection2();
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
                                startCamera();
                                break;
                            case 1:
                                Log.e(TAG,"option b");
                                Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, AppUtils.REQUEST_PICK_PHOTO);
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                });
        builder.show();

    }
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void startCamera() {
        try {
            dispatchTakePictureIntent();
        } catch (IOException e) {
        }
    }
    private void setImageToImageView(String path){
        Log.e(TAG,"setImageToImageView");
        Uri imageUri = Uri.parse(path);
        File file = new File(imageUri.getPath());
        try {
            InputStream mStreamPic = new FileInputStream(file);
            iv_captured_image.setImageBitmap(BitmapFactory.decodeStream(mStreamPic));
            if(mRequest==REQUEST_PICK_PHOTO) AppUtils.convertBitmapToBase64(path);
            else if (mRequest ==REQUEST_TAKE_PHOTO) AppUtils.encodeImage(((BitmapDrawable) iv_captured_image.getDrawable()).getBitmap());
        } catch (FileNotFoundException e) {
            return;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == mActivity.RESULT_OK) {
            mRequest = REQUEST_TAKE_PHOTO;
            // Show the thumbnail on ImageView
            setImageToImageView(mCurrentPhotoPath);

            /*// ScanFile so it will be appeared on Gallery
            MediaScannerConnection.scanFile(mActivity,
                    new String[]{imageUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });*/
        }
        if(requestCode==REQUEST_PICK_PHOTO){
            mRequest = REQUEST_PICK_PHOTO;
            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = mActivity.getContentResolver().query(selectedImage,filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            setImageToImageView(picturePath);
            /*Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            Log.w("path of image from gallery......******************.........", picturePath+"");
            iv_captured_image.setImageBitmap(thumbnail);*/
        }
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                //Uri photoURI = Uri.fromFile(createImageFile());
                mImageCaptureUri = FileProvider.getUriForFile(mActivity,
                        BuildConfig.APPLICATION_ID + ".provider",
                        createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
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
    private void gotoFragmentInspection2() {
        Fragment fragment = new Fragment_Inspection2();
        Bundle data = new Bundle();
        data.putString(AppUtils.ARGS_MODEL,mModelName);
        fragment.setArguments(data);
        FragmentTransaction fragmentTransaction = mManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame_container, fragment,AppUtils.TAG_FRAGMENT_INSPECTION2);
        fragmentTransaction.addToBackStack(AppUtils.TAG_FRAGMENT_INSPECTION2);
        fragmentTransaction.commit();
    }
}
