package com.google.android.gms.fit.samples.basichistoryapi.widget;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.android.gms.fit.samples.basichistoryapi.WorkoutLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by archit.m on 15/08/16.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "WidgetDataProvider";

    List<String> mCollection = new ArrayList<>();
    List<WorkoutLog> workoutLogs = new ArrayList<>();
    Context mContext = null;

    public WidgetDataProvider(Context context,List<WorkoutLog> workoutLogs) {
        mContext = context;
        this.workoutLogs = workoutLogs;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {

    }

    public void onDataSetChanged(List<WorkoutLog> workoutLogs) {
        this.workoutLogs = workoutLogs;
        initData();
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
        Collections.sort(workoutLogs);
        for (WorkoutLog workoutLog : workoutLogs) {
            String exercise = workoutLog.getExercise();
            String weight = String.valueOf(workoutLog.getWeight());
            String repetitions = String.valueOf(workoutLog.getRepetitions());
            mCollection.add(exercise+" "+weight+ " "+repetitions);
        }

    }

}
