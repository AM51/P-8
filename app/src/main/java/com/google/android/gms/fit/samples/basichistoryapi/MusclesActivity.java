package com.google.android.gms.fit.samples.basichistoryapi;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class MusclesActivity extends AppCompatActivity implements MuscleListFragment.MuscleFragmentCallback,ExerciseListFragment.ExerciseFragmentCallback{


    String muscleSelected;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muscles);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getFragmentManager().beginTransaction().replace(R.id.frameLayout,new MuscleListFragment()).addToBackStack(null).commit();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if( count == 2 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemSelected(String muscle) {
        this.muscleSelected = muscle;
    }

    @Override
    public String getSelectedMuscle() {
        return muscleSelected;
    }
}
