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
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fit.samples.utils.Utils;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.fitness.data.Field.FIELD_EXERCISE;
import static com.google.android.gms.fitness.data.Field.FIELD_REPETITIONS;
import static com.google.android.gms.fitness.data.Field.FIELD_RESISTANCE;
import static java.text.DateFormat.getTimeInstance;

public class TodaysWorkoutActivity extends AppCompatActivity {

    ImageButton fetchWorkoutHistory;
    static final int WORKOUT_LOGS_REQUEST = 1;
    public static GoogleApiClient mClient = null;
    private WorkoutLogsAdapter workoutLogsAdapter;
    long currentDayTimeInMillis;
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

        ListView listView = (ListView)findViewById(R.id.todaysWorkoutLogs);
        workoutLogsAdapter = new WorkoutLogsAdapter(getApplicationContext());
        listView.setAdapter(workoutLogsAdapter);

        //long currentDayTimeInMillis = getCurrentDayTimeInMillis();
        currentDayTimeInMillis = 1471199400000L;
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
        //long currentDayTimeInMillis = 1471199400000L;
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
                currentDayTimeInMillis = workoutDate;
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

    private class InsertAndVerifyDataTask extends AsyncTask<Long, Void, List<WorkoutLog>> {

        @Override
        protected List<WorkoutLog> doInBackground(Long... params) {
            Log.e("archit","in async task"+ " "+params[0]);
            DataReadRequest readRequest = WorkoutHistoryHelper.queryWorkoutData(params[0]);
            DataReadResult dataReadResult =
                    Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES);
            Log.e("archit","result fetched: "+dataReadResult.getBuckets().size()+" "+dataReadResult.getDataSets().size());

            if (dataReadResult.getBuckets().size() > 0) {

                for (Bucket bucket : dataReadResult.getBuckets()) {
                    List<DataSet> dataSets = bucket.getDataSets();
                    for (DataSet dataSet : dataSets) {
                        return parseDataset(dataSet);
                    }
                }
            } else if (dataReadResult.getDataSets().size() > 0) {
                for (DataSet dataSet : dataReadResult.getDataSets()) {
                        return parseDataset(dataSet);
                }
            }

            //WorkoutHistoryHelper.printData(dataReadResult);
            return null;
        }


        private List<WorkoutLog> parseDataset(DataSet dataSet){
            DateFormat dateFormat = getTimeInstance();
            List<WorkoutLog> workoutLogs = new ArrayList<>();
            for (DataPoint dp : dataSet.getDataPoints()) {
                WorkoutLog workoutLog = new WorkoutLog();
                for(Field field : dp.getDataType().getFields()) {
                    String fieldName = field.getName();
                    Value dpValue = dp.getValue(field);
                    Log.e("test",fieldName+" "+dpValue.toString());
                    if (       fieldName.equals(FIELD_EXERCISE.getName())) {
                        workoutLog.setExercise(dpValue.toString());
                    } else if( fieldName.equals(FIELD_REPETITIONS.getName())){
                        workoutLog.setRepetitions(Integer.parseInt(dpValue.toString()));
                    } else if( fieldName.equals(FIELD_RESISTANCE.getName())){
                      workoutLog.setWeight(Float.parseFloat(dpValue.toString()));
                    }
                }
                workoutLogs.add(workoutLog);
            }
            return workoutLogs;
        }

        @Override
        protected void onPostExecute(List<WorkoutLog> list) {
            super.onPostExecute(list);
            Collections.sort(list);
            //organiseWorkoutLogs(list);
            workoutLogsAdapter.setWorkoutLogs(list);
            workoutLogsAdapter.notifyDataSetChanged();
        }

        private void organiseWorkoutLogs(List<WorkoutLog> list) {

        }
    }
}
