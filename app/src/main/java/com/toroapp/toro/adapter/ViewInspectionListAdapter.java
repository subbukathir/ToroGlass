package com.toroapp.toro.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toroapp.toro.MyApplication;
import com.toroapp.toro.R;
import com.toroapp.toro.localstorage.entity.InspectionEntity;
import com.toroapp.toro.utils.Font;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewInspectionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ViewInspectionListAdapter.class.getSimpleName();

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private List<InspectionEntity> inspectionEntityList = new ArrayList<>();
    private AppCompatActivity mActivity;
    private Font font;
    private int mSelectedPosition;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    private Date dateStart = new Date();
    private boolean isFooterEnabled = false;

    public ViewInspectionListAdapter(AppCompatActivity mActivity, List<InspectionEntity> inspectionEntities) {
        Log.d( TAG,"ViewComplaintListAdapter");

        this.mActivity = mActivity;
        this.inspectionEntityList = inspectionEntities;
        font = MyApplication.getInstance().getFontInstance();
    }


    public void delete(int position) {
        inspectionEntityList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG,"onCreateViewHolder");
        RecyclerView.ViewHolder mHolder = null;
        if (viewType == VIEW_ITEM) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_inspection, parent, false);
            mHolder = new InspectionListHolder(layoutView);
        } else if (viewType == VIEW_PROG) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_loading_message_list, parent, false);
            mHolder = new LoadingMessageHolder(layoutView);
        }
        return mHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mHolder, final int position) {
        Log.d( TAG,"onBindViewHolder");

        try {
            //Animation animation = AnimationUtils.loadAnimation(mActivity, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            //mHolder.itemView.startAnimation(animation);
            //lastPosition = position; Jun 29, 2017 12:00:00 AM
            if (mHolder instanceof InspectionListHolder) {
                InspectionListHolder holder = (InspectionListHolder) mHolder;
                InspectionEntity item = inspectionEntityList.get(position);
                holder.tv_inspection_name.setText(item.getInspectionName());
                if(item.getTestedValue().equals("Yes")) holder.iv_tested.setImageResource(R.drawable.ic_tick_green);
                else holder.iv_tested.setImageResource(R.drawable.ic_cross_red);
                holder.iv_capturedImage.setImageResource(R.drawable.ic_logo);
            } else if (mHolder instanceof LoadingMessageHolder) {
                LoadingMessageHolder holder = (LoadingMessageHolder) mHolder;
                holder.layout_loading_message.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (isFooterEnabled) ? inspectionEntityList.size() + 1 : inspectionEntityList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (isFooterEnabled && position >= inspectionEntityList.size()) ? VIEW_PROG : VIEW_ITEM;
    }


    public void selectPosition(int position) {
        int lastPosition = mSelectedPosition;
        mSelectedPosition = position;
        notifyItemChanged(lastPosition);
        notifyItemChanged(position);
    }

    public class InspectionListHolder extends RecyclerView.ViewHolder {
        TextView tv_inspection_name;
        ImageView iv_tested,iv_capturedImage;
        View itemView;

        public InspectionListHolder(View itemView) {
            super(itemView);
            try {
                this.itemView = itemView;
                tv_inspection_name = (TextView) itemView.findViewById(R.id.tv_inspection_name);
                iv_tested = (ImageView) itemView.findViewById(R.id.iv_tested_value);
                iv_capturedImage = (ImageView) itemView.findViewById(R.id.iv_captured_image);

                tv_inspection_name.setTypeface(font.getHelveticaRegular());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public class LoadingMessageHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView text_view_loading_message;
        LinearLayout layout_loading_message;

        public LoadingMessageHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            layout_loading_message = (LinearLayout) itemView.findViewById(R.id.layout_loading);
            text_view_loading_message = (TextView) itemView.findViewById(R.id.text_view_message);
            text_view_loading_message.setTypeface(font.getHelveticaRegular());
        }
    }

}
