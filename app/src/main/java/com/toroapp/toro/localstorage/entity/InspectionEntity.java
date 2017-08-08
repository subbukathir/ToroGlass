package com.toroapp.toro.localstorage.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

/**
 * Created by vikram on 7/8/17.
 */
@Entity(tableName = "InspectionEntity")
public class InspectionEntity {

    @PrimaryKey
    @ColumnInfo(name="uniqueKey")
    private String uniqueKey;

    @ColumnInfo(name = "inspectionName")
    private String inspectionName;

    @ColumnInfo(name = "modelName")
    private String modelName;

    @ColumnInfo(name = "testedValue")
    private String testedValue;

    @Nullable
    @ColumnInfo(name = "remarks")
    private String remarks;

    @Nullable
    @ColumnInfo(name = "imageData")
    private String imageData;

    public InspectionEntity(String uniqueKey,String inspectionName, String modelName, String testedValue, String remarks, String imageData) {
        this.uniqueKey = uniqueKey;
        this.inspectionName = inspectionName;
        this.modelName = modelName;
        this.testedValue = testedValue;
        this.remarks = remarks;
        this.imageData = imageData;
    }

    public String getInspectionName() {
        return inspectionName;
    }

    public void setInspectionName(String inspectionName) {
        this.inspectionName = inspectionName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getTestedValue() {
        return testedValue;
    }

    public void setTestedValue(String testedValue) {
        this.testedValue = testedValue;
    }

    @Nullable
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(@Nullable String remarks) {
        this.remarks = remarks;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
