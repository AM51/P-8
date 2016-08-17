/*
 * Copyright (C) 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.fit.samples.basichistoryapi;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fit.samples.basichistoryapi.data.WorkoutLogContract;
import com.google.android.gms.fit.samples.basichistoryapi.widget.WorkoutLogsWidget;
import com.google.android.gms.fit.samples.common.logger.MessageOnlyLogFilter;
import com.google.android.gms.fit.samples.utils.Utils;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.WorkoutExercises;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataUpdateRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

/**
 * This sample demonstrates how to use the History API of the Google Fit platform to insert data,
 * query against existing data, and remove data. It also demonstrates how to authenticate
 * a user with Google Play Services and how to properly represent data in a {@link DataSet}.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.saveExerciseLog) Button saveButton;
    @BindView(R.id.weight_entry) EditText weightEntry;
    @BindView(R.id.reps_entry) EditText repsEntry;
    public static final String TAG = "BasicHistoryApi";
    private static final int REQUEST_OAUTH = 1;
    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";
    private String exercise;


    /**
     *  Track whether an authorization activity is stacking over the current activity, i.e. when
     *  a known auth error is being resolved, such as showing the account chooser or presenting a
     *  consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private static final String AUTH_PENDING = "auth_state_pending";
    private static boolean authInProgress = false;

    public static GoogleApiClient mClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // This method sets up our custom logger, which will print all log messages to the device
        // screen, as well as to adb logcat.

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }


        Intent intent = getIntent();
        exercise = intent.getStringExtra(Utils.EXERCISE_NAME);
        Log.e("archit",exercise+ " wohoo");
        ButterKnife.bind(this);
//        saveButton = (Button)findViewById(R.id.saveExerciseLog);
//        weightEntry = (EditText)findViewById(R.id.weight_entry);
//        repsEntry = (EditText)findViewById(R.id.reps_entry);

        saveButton.setOnClickListener(this);


    }

    /**
     *  Build a {@link GoogleApiClient} that will authenticate the user and allow the application
     *  to connect to Fitness APIs. The scopes included should match the scopes your app needs
     *  (see documentation for details). Authentication will occasionally fail intentionally,
     *  and in those cases, there will be a known resolution, which the OnConnectionFailedListener()
     *  can address. Examples of this include the user never having signed in before, or
     *  having multiple accounts on the device and needing to specify which account to use, etc.
     */
    private void buildFitnessClient(final WorkoutLog workoutLog) {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.  What to do?
                                // Look at some data!!
                                new InsertAndVerifyDataTask().execute(workoutLog);
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.i(TAG, "Google Play services connection failed. Cause: " +
                                result.toString());
                        Log.i(TAG,result.toString());
                        Snackbar.make(
                                MainActivity.this.findViewById(R.id.main_activity_view),
                                "Exception while connecting to Google Play services: " +
                                        result.getErrorMessage(),
                                Snackbar.LENGTH_INDEFINITE).show();
                    }
                })
                .build();
    }

    @Override
    public void onClick(View v) {
//        WorkoutLog workoutLog = new WorkoutLog("test3",10,10.0f);
//        //WorkoutLog workoutLog  = getIntent().getParcelableExtra(Utils.WORKOUT_LOG);
//        Log.e("archit",workoutLog.getExercise()+ " "+workoutLog.getRepetitions()+ " "+workoutLog.getWeight());
//        buildFitnessClient(workoutLog);

        Editable weightEntryText = weightEntry.getText();
        Editable repsEntryText = repsEntry.getText();

        if( (weightEntryText == null || Utils.isEmptyString(weightEntryText.toString())) ||
                (repsEntryText == null || Utils.isEmptyString(repsEntryText.toString())) ){
            Toast.makeText(getApplicationContext(), "Please fill both reps and weight entries", Toast.LENGTH_LONG).show();
        } else {

            float weightLog = Float.parseFloat(weightEntry.getText().toString());
            int repsLog = Integer.parseInt(repsEntry.getText().toString());

            WorkoutLog workoutLog = new WorkoutLog(exercise, repsLog, weightLog);
            buildFitnessClient(workoutLog);

            ContentValues contentValues = new ContentValues();
            contentValues.put(WorkoutLogContract.WLog.COLUMN_LOG_ID, UUID.randomUUID().toString());
            contentValues.put(WorkoutLogContract.WLog.COLUMN_LOG_EXERCISE, exercise);
            contentValues.put(WorkoutLogContract.WLog.COLUMN_LOG_WEIGHT, String.valueOf(weightLog));
            contentValues.put(WorkoutLogContract.WLog.COLUMN_LOG_REPETITIONS, String.valueOf(repsLog));
            contentValues.put(WorkoutLogContract.WLog.COLUMN_TIMESTAMP, new Date().getTime());
            getApplicationContext().getContentResolver().insert(WorkoutLogContract.WLog.CONTENT_URI, contentValues);
            //Intent i = new Intent(getApplicationContext(), WorkoutLogsWidget.class);
            Intent i = new Intent(WorkoutLogsWidget.DATABASE_CHANGED);
            //i.setAction(WorkoutLogsWidget.DATABASE_CHANGED);
            getApplicationContext().sendBroadcast(i);
            Log.e("archit", "Exercise log inserted into db");

        }

    }

    /**
     *  Create a {@link DataSet} to insert data into the History API, and
     *  then create and execute a {@link DataReadRequest} to verify the insertion succeeded.
     *  By using an {@link AsyncTask}, we can schedule synchronous calls, so that we can query for
     *  data after confirming that our insert was successful. Using asynchronous calls and callbacks
     *  would not guarantee that the insertion had concluded before the read request was made.
     *  An example of an asynchronous call using a callback can be found in the example
     *  on deleting data below.
     */
    private class InsertAndVerifyDataTask extends AsyncTask<WorkoutLog, Void, Void> {
        protected Void doInBackground(WorkoutLog... params) {
            // Create a new dataset and insertion request.
            //DataSet dataSet = insertFitnessData();
            WorkoutLog workoutLog = params[0];
            DataSet dataSet = insertWorkoutData(workoutLog);


            // [START insert_dataset]
            // Then, invoke the History API to insert the data and await the result, which is
            // possible here because of the {@link AsyncTask}. Always include a timeout when calling
            // await() to prevent hanging that can occur from the service being shutdown because
            // of low memory or other conditions.
            Log.i(TAG, "Inserting the dataset in the History API.");
            com.google.android.gms.common.api.Status insertStatus =
                    Fitness.HistoryApi.insertData(mClient, dataSet)
                            .await(2, TimeUnit.MINUTES);

            // Before querying the data, check to see if the insertion succeeded.
            if (!insertStatus.isSuccess()) {
                Log.i(TAG, "There was a problem inserting the dataset.");
                return null;
            }

            // At this point, the data has been inserted and can be read.
            Log.i(TAG, "Data insert was successful!");
            return null;
        }
    }

    /**
     * Create and return a {@link DataSet} of step count data for insertion using the History API.
     */

    private DataSet insertWorkoutData(WorkoutLog workoutLog){

        Log.i(TAG, "Creating a new data insert request for workout.");

        Date now = new Date();

        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_WORKOUT_EXERCISE)
                .setStreamName(TAG + " - workout")
                .setType(DataSource.TYPE_RAW)
                .build();

        DataSet dataSet = DataSet.create(dataSource);


        DataPoint curls = DataPoint.create(dataSource);
        curls.setTimeInterval(now.getTime()-3600000,now.getTime(),TimeUnit.MILLISECONDS);
        //curls.setTimestamp(now.getTime(), TimeUnit.MILLISECONDS);
//        curls.getValue(Field.FIELD_EXERCISE).setString(WorkoutExercises.BICEP_CURL);
//        curls.getValue(Field.FIELD_REPETITIONS).setInt(10);
//        curls.getValue(Field.FIELD_RESISTANCE_TYPE).setInt(Field.RESISTANCE_TYPE_DUMBBELL);
//        curls.getValue(Field.FIELD_RESISTANCE).setFloat(20.0f);
//        curls.getValue(Field.FIELD_DURATION).setInt(20);

        curls.getValue(Field.FIELD_EXERCISE).setString(workoutLog.getExercise());
        curls.getValue(Field.FIELD_REPETITIONS).setInt(workoutLog.getRepetitions());
        curls.getValue(Field.FIELD_RESISTANCE_TYPE).setInt(Field.RESISTANCE_TYPE_DUMBBELL);
        curls.getValue(Field.FIELD_RESISTANCE).setFloat(workoutLog.getWeight());
        curls.getValue(Field.FIELD_DURATION).setInt(20);



        dataSet.add(curls);

        return dataSet;

    }



}
