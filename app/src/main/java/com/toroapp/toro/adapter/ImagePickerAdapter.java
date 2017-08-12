package com.toroapp.toro.adapter;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.classes.CustomGallery;
import com.toroapp.toro.utils.AppUtils;
import com.toroapp.toro.utils.Font;

import java.util.ArrayList;

public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.MyViewHolder> {
    public static String MODULE = "ImagePickerAdapter";
    public static String TAG = "";

    ArrayList<CustomGallery> data = new ArrayList<CustomGallery>();
    Fragment mFragment;
    int mPadding;
    int width;
    int height;
    ImageLoader imageLoader;
    DisplayImageOptions options;
    private LayoutInflater inflater;
    private AppCompatActivity mActivity;
    private Font font;
    private boolean isActionMultiplePick;

    public ImagePickerAdapter(Fragment mFragment, ArrayList<CustomGallery> data) {
        TAG = "ImagePickerAdapter";
        Log.d(MODULE, TAG);
        this.mFragment = mFragment;
        mActivity = (AppCompatActivity) mFragment.getActivity();
        inflater = LayoutInflater.from(mActivity);
        this.data = data;
        font = MyApplication.getInstance().getFontInstance();
        imageLoader = ImageLoader.getInstance();
        options = AppUtils.getOptions();
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        mPadding = (int) mActivity.getResources().getDimension(R.dimen.space_layout_padding_small);
        mPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mPadding, new DisplayMetrics());
        mPadding = mPadding / 4;
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TAG = "onCreateViewHolder";
        Log.d(MODULE, TAG);

        View view = inflater.inflate(R.layout.view_item_image_picker, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        TAG = "onBindViewHolder";
        Log.d(MODULE, TAG);
        try {
            CustomGallery current = data.get(position);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width / 2, height / 4);
            params.setMargins(mPadding, mPadding, mPadding, mPadding);
            holder.ll_root.setLayoutParams(params);
            String Str_Path = "file://" + current.sdcardPath;
            Log.d(MODULE, TAG + " Str_Path : " + Str_Path);
            if (current.isSeleted)
                holder.itemView.setBackgroundColor(mActivity.getResources().getColor(R.color.color_black));
            else
                holder.itemView.setBackgroundColor(mActivity.getResources().getColor(R.color.color_white));
            imageLoader.displayImage(Str_Path, holder.iv_list, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    holder.iv_list.setImageResource(R.drawable.no_image_available);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setMultiplePick(boolean isMultiplePick) {
        this.isActionMultiplePick = isMultiplePick;
    }

    public void selectAll(boolean selection) {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).isSeleted = selection;
        }
        notifyDataSetChanged();
    }

    public boolean isAllSelected() {
        boolean isAllSelected = true;

        for (int i = 0; i < data.size(); i++) {
            if (!data.get(i).isSeleted) {
                isAllSelected = false;
                break;
            }
        }

        return isAllSelected;
    }

    public boolean isAnySelected() {
        boolean isAnySelected = false;

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSeleted) {
                isAnySelected = true;
                break;
            }
        }

        return isAnySelected;
    }

    public ArrayList<CustomGallery> getSelected() {
        ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSeleted) {
                dataT.add(data.get(i));
            }
        }

        return dataT;
    }

    public void addAll(ArrayList<CustomGallery> files) {

        try {
            this.data.clear();
            this.data.addAll(files);

        } catch (Exception e) {
            e.printStackTrace();
        }

        notifyDataSetChanged();
    }

    public void changeSelection(View v, int position) {

        data.get(position).isSeleted = !data.get(position).isSeleted;
        ((MyViewHolder) v.getTag()).imgQueueMultiSelected.setSelected(data
                .get(position).isSeleted);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_list;
        ImageView imgQueueMultiSelected;
        LinearLayout ll_root;
        View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            try {
                this.itemView = itemView;
                ll_root = (LinearLayout) itemView.findViewById(R.id.ll_root);
                iv_list = (ImageView) itemView.findViewById(R.id.iv_list);
                imgQueueMultiSelected = (ImageView) itemView.findViewById(R.id.imgQueueMultiSelected);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


}
