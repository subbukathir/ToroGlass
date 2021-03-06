package com.toroapp.toro.localstorage.dbhelper;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.toroapp.toro.listeners.InspectionDataListener;
import com.toroapp.toro.localstorage.database.AppDatabase;
import com.toroapp.toro.localstorage.entity.InspectionEntity;
import com.toroapp.toro.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daemonsoft on 7/25/2017.
 */

public class InspectionDbInitializer {

    private static final String TAG = InspectionDbInitializer.class.getName();
    private static PopulateDbAsync mTask;
    private static int mMode;
    private static int mCount = 0;
    private static String mModelName;
    private static String mVehicleId;
    private static List<InspectionEntity> mInspectionEntityList = new ArrayList<>();
    private static List<String> mVehicleList = new ArrayList<>();
    private static List<String> mModelList = new ArrayList<>();
    private static InspectionEntity mInspectionEntity;
    private static InspectionDataListener mCallback;

    public InspectionDbInitializer(InspectionDataListener callback) {
        mCallback = callback;
    }

    public static void insertAll(@NonNull final AppDatabase db, List<InspectionEntity> inspectionEntities, int mode) {
        mTask = new PopulateDbAsync(db);
        mMode = mode;
        mInspectionEntityList = inspectionEntities;
        mTask.execute();
    }

    public static void insertSingleData(@NonNull final AppDatabase db, InspectionEntity inspectionEntity, int mode) {
        mTask = new PopulateDbAsync(db);
        mMode = mode;
        mInspectionEntity = inspectionEntity;
        mTask.execute();
    }

    public static void getAllModelNames(@NonNull final AppDatabase db, int mode) {
        mTask = new PopulateDbAsync(db);
        mMode = mode;
        mTask.execute();
    }

    public static void getAllDataByModelName(@NonNull final AppDatabase db, String modelName, int mode) {
        mTask = new PopulateDbAsync(db);
        mMode = mode;
        mModelName = modelName;
        mTask.execute();
    }

    public static void getAllDataByVehicleId(@NonNull final AppDatabase db, String modelName, String vehicleId, int mode) {
        mTask = new PopulateDbAsync(db);
        mMode = mode;
        mModelName = modelName;
        mVehicleId = vehicleId;
        mTask.execute();
    }

    private static void insertAll(final AppDatabase db, List<InspectionEntity> inspectionEntities) {
        Log.e(TAG, "insertAll");
        db.inspectionDao().deleteAll();
        db.inspectionDao().insertAll(inspectionEntities);
    }

    private static void insertSingle(final AppDatabase db, InspectionEntity inspectionEntity) {
        Log.e(TAG, "insertSingle");
        mCount = db.inspectionDao().countByUniqueKey(inspectionEntity.getUniqueKey());
        if (mCount > 0) db.inspectionDao().update(inspectionEntity);
        else db.inspectionDao().insert(inspectionEntity);
    }

    private static void getAllByModelName(final AppDatabase db, String modelName) {
        Log.e(TAG, "getAllByModelName");
        try {
            mCount = db.inspectionDao().countByModelName(modelName);
            if (mCount > 0) mVehicleList = db.inspectionDao().getDistictVehicles(modelName);
            else Log.e(TAG, "No data found");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void getAllByVehicleId(final AppDatabase db, String modelName, String vehicleId) {
        Log.e(TAG, "getAllByModelName");
        try {
            mCount = db.inspectionDao().countByVehicleId(vehicleId);
            if (mCount > 0)
                mInspectionEntityList = db.inspectionDao().getAllByVehicleId(modelName, vehicleId);
            else Log.e(TAG, "No data found");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void deleteAll(final AppDatabase db) {
        Log.e(TAG, "deleteAll");
        try {
            mCount = db.inspectionDao().count();
            if (mCount > 0) db.inspectionDao().deleteAll();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void getAllModelNameData(final AppDatabase db) {
        Log.e(TAG, "getAllModelNameData");
        try {
            mCount = db.inspectionDao().count();
            if (mCount > 0) mModelList = db.inspectionDao().getDistictModelName();
            else Log.e(TAG, "No data found");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;

        PopulateDbAsync(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            //populateWithTestData(mDb);
            switch (mMode) {
                case AppUtils.MODE_INSERT:
                    insertSingle(mDb, mInspectionEntity);
                    break;
                case AppUtils.MODE_DELETE_ALL:
                    deleteAll(mDb);
                    break;
                case AppUtils.MODE_GETALL_USING_MODEL:
                    getAllByModelName(mDb, mModelName);
                    break;
                case AppUtils.MODE_GETALL_MODEL:
                    getAllModelNameData(mDb);
                    break;
                case AppUtils.MODE_GETALL_USING_VEHICLE:
                    getAllByVehicleId(mDb, mModelName, mVehicleId);
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            switch (mMode) {
                case AppUtils.MODE_INSERT:
                    break;
                case AppUtils.MODE_GET:
                    break;
                case AppUtils.MODE_GETALL_USING_MODEL:
                    if (mCount > 0)
                        mCallback.onVehicleListSuccess(mVehicleList, AppUtils.MODE_GETALL_USING_MODEL);
                    else mCallback.onDataReceivedErr("No data found");
                    break;
                case AppUtils.MODE_GETALL_USING_VEHICLE:
                    if (mCount > 0) mCallback.onDataReceivedSuccess(mInspectionEntityList);
                    else mCallback.onDataReceivedErr("No data found");
                    break;
                case AppUtils.MODE_GETALL_MODEL:
                    if (mCount > 0)
                        mCallback.onVehicleListSuccess(mModelList, AppUtils.MODE_GETALL_MODEL);
                    else mCallback.onDataReceivedErr("No data found");
                    break;
            }
        }
    }
}
