package com.google.android.gms.fit.samples.basichistoryapi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fit.samples.utils.Utils;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TodaysWorkoutActivity extends AppCompatActivity {

    ImageButton fetchWorkoutHistory;
    static final int WORKOUT_LOGS_REQUEST = 1;
    public static GoogleApiClient mClient = null;
    public static final String TAG = "TodaysWorkoutLogActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todays_workout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fetchWorkoutHistory = (ImageButton) findViewById(R.id.fetchWorkoutHistory);
        fetchWorkoutHistory.setBackground(null);

        buildFitnessClient();
        fetchWorkoutHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("archit", "Fetching History");
                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivityForResult(intent, WORKOUT_LOGS_REQUEST);
            }
        });

        //long currentDayTimeInMillis = getCurrentDayTimeInMillis();
        long currentDayTimeInMillis = 1471113000000L;
        new InsertAndVerifyDataTask().execute(currentDayTimeInMillis);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent intent = new Intent(getApplicationContext(),MusclesActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        long currentDayTimeInMillis = 1471113000000L;
        new InsertAndVerifyDataTask().execute(currentDayTimeInMillis);
    }

    private long getCurrentDayTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        calendar.setTime(now);
        return calendar.getTimeInMillis();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WORKOUT_LOGS_REQUEST) {
            if (resultCode == RESULT_OK) {
                int workoutDay = data.getIntExtra(Utils.WORKOUT_DAY, 1);
                int workoutMonth = data.getIntExtra(Utils.WORKOUT_MONTH, 1);
                int workoutYear = data.getIntExtra(Utils.WORKOUT_YEAR, 2000);
                long workoutDate = data.getLongExtra(Utils.WORKOUT_DATE, 0L);
                Log.e("archit", "Date selected : " + workoutDay + " " + workoutMonth + " " + workoutYear + " " + workoutDate);
                new InsertAndVerifyDataTask().execute(workoutDate);
                //WorkoutHistoryHelper.queryWorkoutData(workoutDate);
            }
        }
    }

    private void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                com.google.android.gms.fit.samples.common.logger.Log.i(TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.  What to do?
                                // Look at some data!!
                                //new InsertAndVerifyDataTask().execute(workoutLog);
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    com.google.android.gms.fit.samples.common.logger.Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    com.google.android.gms.fit.samples.common.logger.Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        com.google.android.gms.fit.samples.common.logger.Log.i(TAG, "Google Play services connection failed. Cause: " +
                                result.toString());
                        com.google.android.gms.fit.samples.common.logger.Log.i(TAG, result.toString());
                        Snackbar.make(
                                TodaysWorkoutActivity.this.findViewById(R.id.main_activity_view),
                                "Exception while connecting to Google Play services: " +
                                        result.getErrorMessage(),
                                Snackbar.LENGTH_INDEFINITE).show();
                    }
                })
                .build();
    }

    private class InsertAndVerifyDataTask extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... params) {
            Log.e("archit","in async task"+ " "+params[0]);
            DataReadRequest readRequest = WorkoutHistoryHelper.queryWorkoutData(params[0]);
            DataReadResult dataReadResult =
                    Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES);
            Log.e("archit","result fetched: "+dataReadResult.getBuckets().size());
            dataReadResult.getBuckets().size();
            WorkoutHistoryHelper.printData(dataReadResult);
            return null;
        }
    }
}
