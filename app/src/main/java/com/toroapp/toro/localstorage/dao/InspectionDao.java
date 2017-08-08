package com.toroapp.toro.localstorage.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.toroapp.toro.localstorage.entity.InspectionEntity;

import java.util.List;

@Dao
public interface InspectionDao {

    @Query("SELECT * FROM InspectionEntity")
    List<InspectionEntity> getAll();

    @Query("SELECT COUNT(*) from InspectionEntity")
    int count();

    @Query("SELECT COUNT(*) from InspectionEntity where uniqueKey LIKE  :uniqueKey")
    int countByUniqueKey(String uniqueKey);

    @Query("SELECT COUNT(*) from InspectionEntity where modelName LIKE  :modelName")
    int countByModelName(String modelName);

    @Query("SELECT * FROM InspectionEntity where modelName LIKE  :modelName")
    List<InspectionEntity> getAllByModelName(String modelName);

    @Insert
    void insertAll(List<InspectionEntity> inspectionEntities);

    @Insert
    void insert(InspectionEntity inspectionEntities);

    @Update
    void update(InspectionEntity inspectionEntities);

    @Delete
    void delete(InspectionEntity inspectionEntity);

    @Query("DELETE FROM InspectionEntity")
    void deleteAll();
}
