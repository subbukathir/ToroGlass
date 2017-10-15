package com.toroapp.toro.utils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.DatePicker;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.StackingBehavior;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.toroapp.toro.R;
import com.toroapp.toro.listeners.DatePickerDialogListener;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by subbu on 7/7/17.
 */

public class AppUtils extends Dialog {
    private static final String MODULE = "AppUtils";
    private static String TAG = AppUtils.class.getSimpleName();
    static Context mContext;
    public static String SHARED_ReceiveComplain_LIST = "ReceiveComplain_LIST";
    public static String FRAGMENT_ReceiveComplaintView = "B_ReceiveComplaintview";

    public AppUtils(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    //Room database variables
    public static final String TORO_DATABASE = "toro_database";

    //Shared preference variables
    public static final String SHARED_PREFS = "shared_preference_toro";
    public static final String SHARED_LOGIN = "shared_login_data";


    /**
     * Global variables
     */

    //Ags variables
    public static final String ARGS_MODEL = "arg_model";
    public static final String ARGS_VEHICLEID = "arg_vehicle_id";

    public static final String ARGS_USERNAME = "arg_username";
    public static final String ARGS_PASSWORD = "arg_password";

    public static final String IS_NETWORK_AVAILABLE = "is_network_available";
    public static final String NETWORK_AVAILABLE = "network_available";
    public static final String NETWORK_NOT_AVAILABLE = "network_not_available";

    public static final int MODE_LOCAL = 000;
    public static final int MODE_SERVER = 111;

    public static final int MODE_INSERT = 2000;
    public static final int MODE_INSERT_ALL = 20001;
    public static final int MODE_GET = 2002;
    public static final int MODE_GETALL = 2003;
    public static final int MODE_DELETE = 2004;
    public static final int MODE_DELETE_ALL = 2005;
    public static final int MODE_GETALL_USING_MODEL = 2006;
    public static final int MODE_GETALL_USING_VEHICLE = 2007;
    public static final int MODE_GETALL_MODEL = 2008;

    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_PICK_PHOTO = 2;

    public final static int ALL_PERMISSIONS_RESULT = 107;

    /**
     * Global fragment tags
     */
    public static final String TAG_FORGOT_PASSWORD = "forgot password";
    public static final String TAG_FRAGMENT_INSPECTION = "fragment inspection";
    public static final String TAG_FRAGMENT_INSPECTION2 = "fragment inspection2";
    public static final String TAG_FRAGMENT_INSPECTION3 = "fragment inspection3";
    public static final String TAG_INSPECTIONLIST = "fragment inspection list";
    public static final String TAG_MANUAL = "fragment manual";
    public static final String TAG_BARCODE = "fragment barcode";
    public static final String TAG_FINAL = "fragment final";
    public static final int CAMERA_FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    @SuppressLint("InlinedApi")
    public static final int CAMERA_FACING_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;

    public static File root = android.os.Environment.getExternalStorageDirectory();
    public static String RootPath = "/Android/data/com.toroapp.toro";
    // SD card image directory
    public static final String PHOTO_ALBUM = AppUtils.root.getAbsolutePath() + AppUtils.RootPath;


    public static int SHARED_INT_DIALOG_PICKER = 1400;
    public static String SHARED_DIALOG_PICKER = "Shared_Dialog_Picker";


    /**
     * global methods
     */
    public static void showDialog(Context context, String StrpositiveText, String StrMsg) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .content(StrMsg)
                .positiveText(StrpositiveText)
                .stackingBehavior(StackingBehavior.ADAPTIVE)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    public static void showDialog(Context context, String StrMsg) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .content(StrMsg)
                .positiveText(R.string.lbl_okay)
                .stackingBehavior(StackingBehavior.ADAPTIVE)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    public static Bitmap decodeBase64toImage(String data) {
        byte[] decodedString = Base64.decode(data, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public static String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        Log.e(TAG, "encoded string ::" + encImage);
        return encImage;
    }

    public static String convertBitmapToBase64(String path) {
        Log.e(TAG, "convertBitmapToBase64");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Log.e(TAG, "encoded string ::" + encImage);
        return encImage;
    }

    static ProgressDialog progressDialog;

    public static void showProgressDialog(AppCompatActivity mActivity, String Str_Msg, boolean setCancelable) {
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(Str_Msg);
        progressDialog.setCancelable(setCancelable);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public static void hideProgressDialog() {
        if (progressDialog == null) {
        } else {
            progressDialog.hide();
            progressDialog.dismiss();
        }
    }

    public static int getIdForRequestedCamera(int facing) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == facing) {
                return i;
            }
        }
        return -1;
    }

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private static Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private static Matcher matcher;

    public static boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //time zone formate "yyyy-MM-dd'T'HH:mm:ss.ZZZZ"
    public static String getDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        Log.e(TAG, "getDateTime :" + simpleDateFormat.format(date));
        return simpleDateFormat.format(date);
    }

    //time zone formate "yyyy-MM-dd'T'HH:mm:ss.ZZZZ"
    public static String getDateTime(Long strUnixformate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date(strUnixformate);
        Log.e(TAG, "getDateTime :" + simpleDateFormat.format(date));
        return simpleDateFormat.format(date);
    }

    //time zone formate "yyyy-MM-dd'T'HH:mm:ss.ZZZZ"
    public static String getDateTime(String inputString) {
        String outputString = inputString;
        try {
            // Convert input string into a date
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
            Date date = inputFormat.parse(inputString);

            // Format date into output format
            DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            outputString = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();

        }
        Log.e(TAG, "getDateTime :" + outputString);
        return outputString;
    }

    public static String getCurrentYear() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        Date date = new Date();
        Log.e(TAG, "getDateTime :" + simpleDateFormat.format(date));
        return simpleDateFormat.format(date);
    }

    public static String getUniqueLogComplaintNo() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
        Date date = new Date();
        Log.e(TAG, "getUniqueLogComplaintNo :" + simpleDateFormat.format(date));
        return simpleDateFormat.format(date);
    }

    public static Object fromJson(String jsonString, Type type) {
        return new Gson().fromJson(jsonString, type);
    }

    public static boolean IsFileExist(File dir, File file) {
        TAG = "IsFileExist";
        Log.d(MODULE, TAG);

        boolean RetValue = false;
        File[] Files_Array = dir.listFiles();
        if (Files_Array != null) {
            if (Files_Array.length > 0) {
                for (int i = 0; i < Files_Array.length; i++) {
                    if (file.equals(Files_Array[i])) {
                        RetValue = true;
                    }
                }
            }
        }
        return RetValue;
    }

    public static void saveImage(Bitmap photo, AppCompatActivity mActivity, String Str_Name) {
        File sdIconStorageDir = new File(getProfilePicturePath(mActivity));
        sdIconStorageDir.mkdirs();
        try {
            String filePath = sdIconStorageDir.toString() + "/" + Str_Name + ".png";
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("TAG", "Error saving image file: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG", "Error saving image file: " + e.getMessage());
        }
    }

    public static String getProfilePicturePath(Context context) {
        String profilePicturePath = AppUtils.PHOTO_ALBUM;
        return profilePicturePath;
    }

    public static Bitmap getScaledBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    static Calendar newCalendar = Calendar.getInstance();
    static String strDate = null;
    static DatePickerDialogListener mCallback = null;

    public static String datePickerDialog(AppCompatActivity mActivity, DatePickerDialogListener listener) {
        mCallback = listener;
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                strDate = (simpleDateFormat.format(newDate.getTime()));
                Log.e(TAG, "datePickerDialog " + strDate);
                mCallback.onDateReceivedSuccess(strDate);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)).show();
        return strDate;
    }

    public static DisplayImageOptions getOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(0)).cacheInMemory(true)
                .cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.no_image_available)
                .showImageForEmptyUri(R.drawable.no_image_available)
                .showImageOnFail(R.drawable.no_image_available)
                .resetViewBeforeLoading(true).considerExifParams(true).build();
        return options;

    }


}
