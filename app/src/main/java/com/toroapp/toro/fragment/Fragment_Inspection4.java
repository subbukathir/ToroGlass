package com.toroapp.toro.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.soundcloud.android.crop.BuildConfig;
import com.soundcloud.android.crop.Crop;
import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.classes.Action;
import com.toroapp.toro.listeners.ImagePickListener;
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

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.toroapp.toro.utils.AppUtils.ALL_PERMISSIONS_RESULT;
import static com.toroapp.toro.utils.AppUtils.REQUEST_PICK_PHOTO;
import static com.toroapp.toro.utils.AppUtils.REQUEST_TAKE_PHOTO;

/**
 * Created by subbu on 25/11/16.
 */

public class Fragment_Inspection4 extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener,
        InspectionDataListener,ImagePickListener{
    private static final String MODULE = Fragment_Inspection4.class.getSimpleName();
    private static String TAG = "";

    private AppCompatActivity mActivity;
    private Context mContext;
    private Font font = MyApplication.getInstance().getFontInstance();
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private FragmentManager mManager;
    private Handler mHandler;

    private TextView tv_model_name,tv_lbl_title_vehicle_id, tv_lbl_basic_check, tv_lbl_quesno, tv_lbl_question;
    private RadioGroup radioGroup;
    private RadioButton rdb_yes, rdb_no;
    private EditText et_remarks;
    private Button btn_capture_photo,btnNext;
    private ImageView iv_captured_image;
    private Uri mImageCaptureUri;
    private String mStringJson = null;

    Bitmap myBitmap;
    Uri picUri;
    private String mUniqueKey=null, mModelName=null, mVehicleId=null,mInspectionName=null,mRemarks =null,mTested=null,mImageData = null;
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

    private ImageLoader imageLoader;

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
            imageLoader = ImageLoader.getInstance();
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
            rootView = (View) inflater.inflate(R.layout.fragment_inspect2, container, false);
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
            tv_lbl_title_vehicle_id = (TextView) rootView.findViewById(R.id.tv_lbl_title_vehicle_id);
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
        tv_lbl_title_vehicle_id.setTypeface(font.getHelveticaRegular());
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
                mVehicleId = mArgs.getString(AppUtils.ARGS_VEHICLEID);
                mInspectionName = getString(R.string.lbl_ques_2);
                mInspectionName = mInspectionName.substring(3);
                mUniqueKey = mInspectionName + mModelName+mVehicleId;
                Log.e(TAG,"Substring ins name :"+ mInspectionName + ": mVehicleId :"+mVehicleId);
                tv_model_name.setText(mModelName);
                tv_lbl_title_vehicle_id.setText(mVehicleId);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_capture_photo:
                ShowSelectPhotoOption1();
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
                    mRemarks = et_remarks.getText().toString().trim();
                    InspectionEntity inspectionEntity = new InspectionEntity(mUniqueKey,mInspectionName,mModelName,mTested,mRemarks,mImageData,mVehicleId);
                    mInspectionDb.insertSingleData(AppDatabase.getAppDatabase(mActivity),inspectionEntity,AppUtils.MODE_INSERT);
                    gotoFragmentInspection2();
                }else AppUtils.showDialog(mActivity,getString(R.string.msg_choose_yes_or_no));
            }else AppUtils.showDialog(mActivity,getString(R.string.msg_inspection_name_empty));
        }else AppUtils.showDialog(mActivity,getString(R.string.msg_model_name_empty));
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
                Uri photoURI = FileProvider.getUriForFile(mActivity,
                        BuildConfig.APPLICATION_ID + ".provider",
                        createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                mActivity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
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
        Fragment fragment = new Fragment_InspectionLast();
        Bundle data = new Bundle();
        data.putString(AppUtils.ARGS_MODEL,mModelName);
        data.putString(AppUtils.ARGS_VEHICLEID,mVehicleId);
        fragment.setArguments(data);
        FragmentTransaction fragmentTransaction = mManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame_container, fragment,AppUtils.TAG_FRAGMENT_INSPECTION3);
        fragmentTransaction.addToBackStack(AppUtils.TAG_FRAGMENT_INSPECTION3);
        fragmentTransaction.commit();
    }
    /**
     *
     * Vikram's code
     */
    public void ShowSelectPhotoOption1() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.lbl_select_photo)
                .setItems(R.array.array_select_photo, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //dispatchTakePictureIntent1();
                                gotoFragmentImagePicker();
                                break;
                            case 1:
                                gotoFragmentImagePicker();
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                });
        builder.show();

    }
    public void gotoFragmentImagePicker() {
        TAG = "gotoFragmentImagePicker";
        Log.d(MODULE, TAG);

        try {
            mManager = mActivity.getSupportFragmentManager();
            Bundle Args = new Bundle();
            Args.putString("B_ACTION", Action.ACTION_PICK);
            Fragment_ImagePicker fragment = new Fragment_ImagePicker();
            fragment.setArguments(Args);
            //fragment.setTargetFragment(fragment, AppUtils.SHARED_INT_DIALOG_PICKER);
            fragment.SetImagePickListener(this);
            FragmentTransaction ObjTransaction = mManager.beginTransaction();
            ObjTransaction.add(android.R.id.content, fragment, AppUtils.SHARED_DIALOG_PICKER + "");
            ObjTransaction.addToBackStack(null);
            ObjTransaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void onSingleImagePicked(String Str_Path)
    {
        TAG = "onSingleImagePicked";
        Log.d(MODULE, TAG);
        Log.d(MODULE, TAG + " Single Path : " + Str_Path);

        try
        {
            Str_Path = "file://" + Str_Path;

            imageLoader.displayImage(Str_Path, iv_captured_image, AppUtils.getOptions(), new ImageLoadingListener()
            {
                @Override
                public void onLoadingStarted(String imageUri, View view)
                {
                    Log.d(MODULE, TAG + " onLoadingStarted");
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason)
                {
                    Log.d(MODULE, TAG + " onLoadingFailed");
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                {
                    Log.d(MODULE, TAG + " onLoadingComplete");
                    if(view.getVisibility()==View.GONE)view.setVisibility(View.VISIBLE);
                    mImageData = AppUtils.encodeImage(loadedImage);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view)                {
                    Log.d(MODULE, TAG + " onLoadingCancelled");
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void onMultipleImagePicked(String[] Str_Path) {
        TAG = "onMultipleImagePicked";
        Log.d(MODULE, TAG);

    }
    private void dispatchTakePictureIntent1() {
        TAG = "dispatchTakePictureIntent";
        Log.d(MODULE, TAG);
        try {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        /*mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tmp.jpg"));*/
            mImageCaptureUri = FileProvider.getUriForFile(mActivity,
                    com.toroapp.toro.BuildConfig.APPLICATION_ID + ".provider",
                    createImageFile());
            //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            captureIntent.putExtra("return-data", true);
            startActivityForResult(captureIntent, 101);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        TAG = "onActivityResult";
        Log.d(MODULE, TAG + " requestCode ::: " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == mActivity.RESULT_OK) {
            //beginCrop(mImageCaptureUri);
            String Str_Path = "file://" + Environment.getExternalStorageDirectory() + "/tmp.png";
            clearImageCache(Str_Path);
        } else if (requestCode == 10) {
            handleCrop(resultCode, data);
        }

    }

    private void beginCrop(Uri source) {
        String Str_ImagePath = AppUtils.getProfilePicturePath(mActivity) + "/tmp.png";
        Uri destination = Uri.fromFile(new File(Str_ImagePath));
        Crop.of(source, destination).withMaxSize(320, 320).start(mActivity, this, 10);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == getActivity().RESULT_OK) {
            try {

                String Str_Path = "file://" + AppUtils.getProfilePicturePath(mActivity) + "/tmp.png";
                clearImageCache(Str_Path);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (resultCode == Crop.RESULT_ERROR) {
            Log.d(TAG, MODULE + ":::" + Crop.getError(result).getMessage());
        }
    }

    public void clearImageCache(String imageUri) {
        List<String> listImages = MemoryCacheUtils.findCacheKeysForImageUri(imageUri, imageLoader.getMemoryCache());
        if (listImages != null) {
            if (listImages.size() > 0) {
                MemoryCacheUtils.removeFromCache(imageUri, imageLoader.getMemoryCache());
                DiskCacheUtils.removeFromCache(imageUri, imageLoader.getDiskCache());
            }
        }
    }

    @Override
    public void onVehicleListSuccess(List<String> vehicleList) {

    }

}
