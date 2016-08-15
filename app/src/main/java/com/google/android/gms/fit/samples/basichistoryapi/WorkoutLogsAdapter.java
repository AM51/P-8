package com.google.android.gms.fit.samples.basichistoryapi;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by archit.m on 15/08/16.
 */
public class WorkoutLogsAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    List<WorkoutLog> workoutLogs;

    public WorkoutLogsAdapter(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        workoutLogs = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return workoutLogs.size();
    }

    @Override
    public WorkoutLog getItem(int position) {
        return workoutLogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("test","Inside get view method");
//        TextView textView = new TextView(context);
//        textView.setText("Testing");
//        textView.setTextColor(Color.BLACK);
//        textView.setHeight(100);
//        //Log.v("archit",""+position+" "+strings.get(position));
//        return textView;

        View cardView = layoutInflater.inflate(R.layout.sample_card, null);
        TextView exercise = (TextView) cardView.findViewById(R.id.exercise_log);
        TextView weight = (TextView) cardView.findViewById(R.id.weight_log);
        TextView reps = (TextView) cardView.findViewById(R.id.reps_log);
        WorkoutLog workoutLog = workoutLogs.get(position);

        exercise.setText(workoutLog.getExercise());
        weight.setText(String.valueOf(workoutLog.getWeight()));
        reps.setText(String.valueOf(workoutLog.getRepetitions()));

        return cardView;
    }

    public void setWorkoutLogs(List<WorkoutLog> workoutLogs) {
        this.workoutLogs = workoutLogs;
    }
}
