package com.toroapp.toro.localstorage.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.toroapp.toro.localstorage.dao.InspectionDao;
import com.toroapp.toro.localstorage.entity.InspectionEntity;
import com.toroapp.toro.localstorage.entity.ReceiveComplaintItemEntity;
import com.toroapp.toro.utils.AppUtils;

@Database(entities = {InspectionEntity.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract InspectionDao inspectionDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, AppUtils.TORO_DATABASE)
                    // allow queries on the main thread.
                    // Don't do this on a real app! See PersistenceBasicSample for an example.
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
