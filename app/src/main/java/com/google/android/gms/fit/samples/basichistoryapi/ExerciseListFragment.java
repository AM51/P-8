package com.google.android.gms.fit.samples.basichistoryapi;

import android.annotation.TargetApi;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.fit.samples.basichistoryapi.data.WorkoutExerciseContract;
import com.google.android.gms.fit.samples.utils.Utils;
import com.google.android.gms.fitness.data.WorkoutExercises;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by archit.m on 02/08/16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ExerciseListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>,AdapterView.OnItemClickListener{

    private static final String selection = WorkoutExerciseContract.WorkoutExercise.TABLE_NAME+"."+ WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_CATEGORY+"= ?";
    String selectedMuscle;
    ExerciseListAdapter exerciseListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.exercise_list_fragment, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        selectedMuscle = ((ExerciseFragmentCallback) getActivity()).getSelectedMuscle();
        Log.e("test","Selected Muscle :: "+selectedMuscle);
        exerciseListAdapter = new ExerciseListAdapter(getActivity().getApplicationContext(), selectedMuscle);
        setListAdapter(exerciseListAdapter);
        getListView().setOnItemClickListener(this);
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//

        String exercise = (String) this.getListAdapter().getItem(position);
        Log.e("archit",exercise);
        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
        intent.putExtra(Utils.EXERCISE_NAME,exercise);
        startActivity(intent);
//        Intent intent = new Intent(getActivity().getApplicationContext(),MainActivity.class);
//        //todo here put mapping from ui text to workout exercise
//        WorkoutLog workoutLog = new WorkoutLog(WorkoutExercises.BICEP_CURL,45,20.0f);
//        CharSequence exercise = ((TextView) view).getText();
//        Log.e("archit",""+exercise);
//        //intent.putExtra(Utils.EXERCISE_NAME,""+exercise);
//        intent.putExtra(Utils.EXERCISE_NAME,workoutLog);
//        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

       // Cursor cursor = context.getContentResolver().query(WorkoutExerciseContract.WorkoutExercise.CONTENT_URI, null, selection, new String[]{muscle}, null);
        return new CursorLoader(getActivity(),
                                WorkoutExerciseContract.WorkoutExercise.CONTENT_URI,null,selection,new String[]{selectedMuscle},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.e("test","Inside on Load Finished");
        int ind = cursor.getColumnIndex(WorkoutExerciseContract.WorkoutExercise.COLUMN_EXERCISE_DISPLAY_NAME);
        ArrayList<String> exerciseList = new ArrayList<>();

        while(cursor.moveToNext()) {
            String exerciseName = cursor.getString(ind);
            Log.e("test","exercise fetched :: "+exerciseName);
            exerciseList.add(exerciseName);
        }
        exerciseListAdapter.setStrings(exerciseList,selectedMuscle);
        exerciseListAdapter.notifyDataSetChanged();
        Log.e("test","Loading finished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface ExerciseFragmentCallback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public String getSelectedMuscle();
    }
}
