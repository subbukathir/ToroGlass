package com.toroapp.toro.listeners;

import com.toroapp.toro.localstorage.entity.InspectionEntity;

import java.util.List;

/**
 * Created by vikram on 7/8/17.
 */

public interface InspectionDataListener {
    void onDataReceivedSuccess(List<InspectionEntity> inspectionEntityList);
    void onDataReceivedErr(String strErr);
}
