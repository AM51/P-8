package com.google.android.gms.fit.samples.basichistoryapi;

import android.annotation.TargetApi;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.fit.samples.utils.Utils;
import com.google.android.gms.fitness.data.WorkoutExercises;

/**
 * Created by archit.m on 02/08/16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ExerciseListFragment extends ListFragment implements AdapterView.OnItemClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.exercise_list_fragment, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setListAdapter(new ExerciseListAdapter(getActivity().getApplicationContext()));
        getListView().setOnItemClickListener(this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity().getApplicationContext(),MainActivity.class);
        //todo here put mapping from ui text to workout exercise
        WorkoutLog workoutLog = new WorkoutLog(WorkoutExercises.BICEP_CURL,50,20.0f);
        CharSequence exercise = ((TextView) view).getText();
        Log.e("archit",""+exercise);
        //intent.putExtra(Utils.EXERCISE_NAME,""+exercise);
        intent.putExtra(Utils.EXERCISE_NAME,workoutLog);
        startActivity(intent);
    }
}
