package com.google.android.gms.fit.samples.basichistoryapi.widget;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.android.gms.fit.samples.basichistoryapi.WorkoutLog;
import com.google.android.gms.fit.samples.basichistoryapi.data.WorkoutExerciseContract;
import com.google.android.gms.fit.samples.basichistoryapi.data.WorkoutLogContract;
import com.google.android.gms.fit.samples.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by archit.m on 15/08/16.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "WidgetDataProvider";
    private static final String selection = WorkoutLogContract.WLog.TABLE_NAME+"."+ WorkoutLogContract.WLog.COLUMN_TIMESTAMP+"> ?";


    ContentResolver contentResolver;
    List<String> mCollection = new ArrayList<>();
    Context mContext = null;

    public WidgetDataProvider(Context context,ContentResolver contentResolver) {
        mContext = context;
        this.contentResolver = contentResolver;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        Log.e("archit","Inside on data set changed");

        Thread thread = new Thread() {
            public void run() {
                initData();
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
        }
        //initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mCollection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                android.R.layout.simple_list_item_1);
        view.setTextViewText(android.R.id.text1, mCollection.get(position));
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {

        mCollection.clear();
        List<WorkoutLog> workoutLogs = getWorkoutLogs();
        Collections.sort(workoutLogs);
        for (WorkoutLog workoutLog : workoutLogs) {
            String exercise = workoutLog.getExercise();
            String weight = String.valueOf(workoutLog.getWeight());
            String repetitions = String.valueOf(workoutLog.getRepetitions());
            mCollection.add(exercise+" "+weight+ " "+repetitions);
        }

    }

    private List<WorkoutLog> getWorkoutLogs() {
        long timestampForStartOfToday = Utils.getTimeStampForStartOfToday();
        Cursor cursor = contentResolver.query(WorkoutLogContract.WLog.buildUri(), null, selection, new String[]{String.valueOf(timestampForStartOfToday)}, null);
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
}
