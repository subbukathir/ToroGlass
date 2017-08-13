package com.toroapp.toro.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.support.v7.app.AppCompatDialog;
import android.widget.TextView;

import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.adapter.ImagePickerAdapter;
import com.toroapp.toro.classes.Action;
import com.toroapp.toro.classes.CustomGallery;
import com.toroapp.toro.listeners.ClickListener;
import com.toroapp.toro.listeners.ImagePickListener;
import com.toroapp.toro.listeners.RecyclerTouchListener;
import com.toroapp.toro.utils.Font;

import java.util.ArrayList;
import java.util.Collections;


public class Fragment_ImagePicker extends DialogFragment {
    public static String MODULE = "Fragment_ImagePicker";
    public static String TAG = "";

    Font font = MyApplication.getInstance().getFontInstance();
    AppCompatActivity mActivity;
    FragmentManager mManager;
    CoordinatorLayout cl_main;
    AppBarLayout appbar_layout;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsing_toolbar;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    Button btn_ok;
    ImagePickerAdapter adapter;
    ImagePickListener mCallBack;
    String action = "";
    ArrayList<CustomGallery> mList = new ArrayList<CustomGallery>();
    Bundle mArgs;
    Drawable drawableBack;
    View rootView;
    String[] Str_Path_Array;
    int mSelectedCount = 0;
    int mCount = 3;
    View.OnClickListener _OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                default:
                    break;
            }
        }
    };
    RecyclerTouchListener _OnRecyclerTouchListener = new RecyclerTouchListener(mActivity, recyclerView, new ClickListener() {
        @Override
        public void onClick(View view, int position) {
            if (action.equalsIgnoreCase(Action.ACTION_MULTIPLE_PICK)) {
                TAG = "onClick";
                Log.d(MODULE, TAG);

                Log.d(MODULE, TAG + " " + action);
                mList.get(position).isSeleted = mList.get(position).isSeleted != true;
                adapter.notifyItemChanged(position);
                ArrayList<CustomGallery> mTempList = adapter.getSelected();
                Log.d(MODULE, TAG + " " + "mTempList.size():::" + (mTempList.size()));
                Log.d(MODULE, TAG + " " + "mCount :::" + (mCount));
                if (mTempList.size() == mCount) {
                    Str_Path_Array = new String[mTempList.size()];
                    for (int i = 0; i < mTempList.size(); i++) {
                        Str_Path_Array[i] = mTempList.get(i).sdcardPath;
                    }
                    mCallBack.onMultipleImagePicked(Str_Path_Array);
                    DismissDialog();
                }
            } else if (action.equalsIgnoreCase(Action.ACTION_PICK)) {
                String Str_Path = mList.get(position).sdcardPath;
                mCallBack.onSingleImagePicked(Str_Path);
                DismissDialog();
            }
        }

        @Override
        public void onLongClick(View view, int position) {

        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            TAG = "onCreate";
            Log.d(MODULE, TAG);
            mActivity = (AppCompatActivity) getActivity();
            mManager = mActivity.getSupportFragmentManager();
            setHasOptionsMenu(true);
            setRetainInstance(false);
            mArgs = getArguments();
            if (mArgs != null) {
                Log.d(MODULE, TAG + " " + mArgs);
                action = mArgs.getString("B_ACTION");
                mCount = mArgs.getInt("B_COUNT");
                if (mCount == 0) mCount = 3;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawableBack = mActivity.getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp, null);
            } else {
                drawableBack = mActivity.getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            }
            drawableBack.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            mList = getGalleryPhotos();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        TAG = "onCreateDialog";
        Log.d(MODULE, TAG);
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_image_picker, container, false);
        TAG = "onCreateView";
        Log.d(MODULE, TAG);
        initView(rootView);
        return rootView;
    }

    public void initView(View view) {
        TAG = "initView";
        Log.d(MODULE, TAG);
        try {

            toolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
            cl_main = (CoordinatorLayout) mActivity.findViewById(R.id.cl_main);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            btn_ok = (Button) view.findViewById(R.id.btn_ok);
            setProperties();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setActionBar() {
        TAG = "setActionBar";
        Log.d(MODULE, TAG);

        try {
            toolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.lbl_select_image);
            toolbar.setNavigationIcon(drawableBack);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DismissDialog();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        TAG = "onStart";
        Log.d(MODULE, TAG);

        try {
            setActionBar();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setProperties() {
        TAG = "setProperties";
        Log.d(MODULE, TAG);
        try {
            //appbar_layout.setExpanded(true);
            setManager();
            setData();
            btn_ok.setTypeface(font.getHelveticaRegular());
            rootView.findViewById(R.id.ll_bottom_container).setVisibility(View.GONE);
            if (action.equalsIgnoreCase(Action.ACTION_MULTIPLE_PICK)) {
                Log.d(MODULE, TAG + " " + action);
                adapter.setMultiplePick(true);
            } else if (action.equalsIgnoreCase(Action.ACTION_PICK)) {
                Log.d(MODULE, TAG + " " + action);
                adapter.setMultiplePick(false);
            }
            recyclerView.addOnItemTouchListener(_OnRecyclerTouchListener);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setManager() {
        TAG = "setManager";
        Log.d(MODULE, TAG);
        try {
            mLayoutManager = new GridLayoutManager(mActivity, 2);
            recyclerView.setLayoutManager(mLayoutManager);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ArrayList<CustomGallery> getGalleryPhotos() {
        TAG = "getGalleryPhotos";
        Log.d(MODULE, TAG);

        ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();
        try {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;
            Cursor imagecursor = mActivity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
            Log.d(MODULE, TAG + " imagecursor : " + imagecursor.getCount());
            if (imagecursor != null && imagecursor.getCount() > 0) {
                while (imagecursor.moveToNext()) {
                    CustomGallery item = new CustomGallery();
                    int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    item.sdcardPath = imagecursor.getString(dataColumnIndex);
                    galleryList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        return galleryList;
    }

    public void setData() {
        TAG = "setData";
        Log.d(MODULE, TAG);

        try {
            Log.d(MODULE, TAG + " mList : " + mList.size());
            if (mList.size() > 0) {
                adapter = new ImagePickerAdapter(this, mList);
                recyclerView.setAdapter(adapter);
            } else {

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void SetImagePickListener(ImagePickListener mCallBack) {
        this.mCallBack = mCallBack;
    }

    public void DismissDialog() {
        this.dismiss();
        if (mManager != null) mManager.popBackStack();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        TAG = "onOptionsItemSelected";
        Log.d(MODULE, TAG);

        switch (item.getItemId()) {
            case android.R.id.home:
                mManager.popBackStack();
                return true;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
