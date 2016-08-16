package com.google.android.gms.fit.samples.basichistoryapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;

import com.google.android.gms.fit.samples.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CalendarActivity extends AppCompatActivity {


    @BindView(R.id.calendar) CalendarView calendarView;
    long dateSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
//        calendarView = (CalendarView)findViewById(R.id.calendar);
        dateSelected = calendarView.getDate();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                long currentDateSelected = view.getDate();
                if(currentDateSelected != dateSelected) {
                    Log.e("archit", "" + dayOfMonth + " " + (month+1) + " " + year);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Utils.WORKOUT_DAY, dayOfMonth);
                    returnIntent.putExtra(Utils.WORKOUT_MONTH, month + 1);
                    returnIntent.putExtra(Utils.WORKOUT_YEAR, year);
                    returnIntent.putExtra(Utils.WORKOUT_DATE,calendarView.getDate());
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        });


    }

}
