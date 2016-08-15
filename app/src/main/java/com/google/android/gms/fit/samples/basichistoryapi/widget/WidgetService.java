package com.google.android.gms.fit.samples.basichistoryapi.widget;


import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViewsService;

import com.google.android.gms.fit.samples.basichistoryapi.WorkoutLog;
import com.google.android.gms.fit.samples.basichistoryapi.data.WorkoutLogContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by archit.m on 15/08/16.
 */
public class WidgetService extends RemoteViewsService implements Loader.OnLoadCompleteListener{

    CursorLoader mCursorLoader;
    WidgetDataProvider widgetDataProvider;
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        List<WorkoutLog> workoutLogList = getWorkoutLogs();
        widgetDataProvider = new WidgetDataProvider(this, workoutLogList);
        return widgetDataProvider;
    }

    @NonNull
    private List<WorkoutLog> getWorkoutLogs() {
        Cursor cursor = getContentResolver().query(WorkoutLogContract.WLog.buildUri(), null, null, null, null);
        List<WorkoutLog> workoutLogList = new ArrayList<>();
        WorkoutLog workoutLog;
        while(cursor.moveToNext()){
            int indexExercise = cursor.getColumnIndex(WorkoutLogContract.WLog.COLUMN_LOG_EXERCISE);
            int indexWeight  = cursor.getColumnIndex(WorkoutLogContract.WLog.COLUMN_LOG_WEIGHT);
            int indexReps = cursor.getColumnIndex(WorkoutLogContract.WLog.COLUMN_LOG_REPETITIONS);
            String exercise = cursor.getString(indexExercise);
            String weight = cursor.getString(indexWeight);
            String reps = cursor.getString(indexReps);
            workoutLog = new WorkoutLog(exercise,Integer.parseInt(reps),Float.valueOf(weight));
            workoutLogList.add(workoutLog);
            Log.e("archit","Data Fetched From db "+exercise+" "+weight);
        }
        return workoutLogList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mCursorLoader = new CursorLoader(getApplicationContext(),WorkoutLogContract.WLog.CONTENT_URI, null, null, null, null);
        mCursorLoader.registerListener(0, this);
        mCursorLoader.startLoading();
    }

    @Override
    public void onLoadComplete(Loader loader, Object data) {
        Log.e("archit","data set changed");
        List<WorkoutLog> workoutLogs = getWorkoutLogs();
        Log.e("archit","Workout Logs Size :: "+workoutLogs.size());
        widgetDataProvider.onDataSetChanged(workoutLogs);
    }

    @Override
    public void onDestroy() {

        if (mCursorLoader != null) {
            mCursorLoader.unregisterListener(this);
            mCursorLoader.cancelLoad();
            mCursorLoader.stopLoading();
        }

    }


}
