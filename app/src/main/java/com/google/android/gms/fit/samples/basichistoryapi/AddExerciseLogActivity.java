package com.google.android.gms.fit.samples.basichistoryapi;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fit.samples.basichistoryapi.R;
import com.google.android.gms.fit.samples.basichistoryapi.data.WorkoutExerciseContract;
import com.google.android.gms.fit.samples.basichistoryapi.data.WorkoutLogContract;
import com.google.android.gms.fit.samples.basichistoryapi.widget.WidgetDataProvider;
import com.google.android.gms.fit.samples.basichistoryapi.widget.WorkoutLogsWidget;
import com.google.android.gms.fit.samples.utils.Utils;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
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

public class AddExerciseLogActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.saveExerciseLog) Button saveButton;
    @BindView(R.id.weight_entry) EditText weightEntry;
    @BindView(R.id.reps_entry) EditText repsEntry;
    public static GoogleApiClient mClient = null;


    private int reps;
    private float weight;
    private String exercise;

    private final static String TAG =  "AddExerciseLog";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarExerciseLog);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        exercise = intent.getStringExtra(Utils.EXERCISE_NAME);
        Log.e("archit",exercise+ " wohoo");
        ButterKnife.bind(this);
//        saveButton = (Button)findViewById(R.id.saveExerciseLog);
//        weightEntry = (EditText)findViewById(R.id.weight_entry);
//        repsEntry = (EditText)findViewById(R.id.reps_entry);

        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        Editable weightEntryText = weightEntry.getText();
        Editable repsEntryText = repsEntry.getText();

        if( (weightEntryText == null || Utils.isEmptyString(weightEntryText.toString())) ||
                (repsEntryText == null || Utils.isEmptyString(repsEntryText.toString())) ){
            Toast.makeText(getApplicationContext(), "Please fill both reps and weight entries", Toast.LENGTH_LONG).show();
        } else {

            float weightLog = Float.parseFloat(weightEntry.getText().toString());
            int repsLog = Integer.parseInt(repsEntry.getText().toString());
            //insertUsingAsyncTask(weightLog, repsLog);
            insertUsingActivity(weightLog, repsLog);
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

    private void insertUsingActivity(float weightLog, int repsLog) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        WorkoutLog workoutLog = new WorkoutLog(exercise, repsLog, weightLog); //todo add exercise name , weight , reps
        intent.putExtra(Utils.WORKOUT_LOG, workoutLog);
        startActivity(intent);
    }

    private void insertUsingAsyncTask(float weightLog, int repsLog) {
        WorkoutLog workoutLog = new WorkoutLog(exercise, repsLog, weightLog);
        buildFitnessClient(workoutLog);
    }

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
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .enableAutoManage(this, 1, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.i(TAG, "Google Play services connection failed. Cause: " +
                                result.toString());
                        Log.i(TAG,result.toString());

                    }
                })
                .build();
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
            // [END insert_dataset]

            // Begin by creating the query.
//            DataReadRequest readRequest = queryFitnessData();
            DataReadRequest readRequest = queryWorkoutData();

            // [START read_dataset]
            // Invoke the History API to fetch the data with the query and await the result of
            // the read request.
            DataReadResult dataReadResult =
                    Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES);
            // [END read_dataset]

            // For the sake of the sample, we'll print the data so we can see what we just added.
            // In general, logging fitness information should be avoided for privacy reasons.
            printData(dataReadResult);

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

    private DataSet insertFitnessData() {
        Log.i(TAG, "Creating a new data insert request.");

        // [START build_insert_data_request]
        // Set a start and end time for our data, using a start time of 1 hour before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        long startTime = cal.getTimeInMillis();

        //DataType.TYPE_WORKOUT_EXERCISE;
        // Create a data source
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setStreamName(TAG + " - step count")
                .setType(DataSource.TYPE_RAW)
                .build();

        // Create a data set
        int stepCountDelta = 950;
        DataSet dataSet = DataSet.create(dataSource);
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        DataPoint dataPoint = dataSet.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataSet.add(dataPoint);
        // [END build_insert_data_request]

        return dataSet;
    }


    public static DataReadRequest queryWorkoutData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_WORKOUT_EXERCISE)
                .build();



        return readRequest;
    }

    /**
     * Return a {@link DataReadRequest} for all step count changes in the past week.
     */
    public static DataReadRequest queryFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                // The data request can specify multiple data types to return, effectively
                // combining multiple data queries into one call.
                // In this example, it's very unlikely that the request is for several hundred
                // datapoints each consisting of a few steps and a timestamp.  The more likely
                // scenario is wanting to see how many steps were walked per day, for 7 days.
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                // bucketByTime allows for a time span, whereas bucketBySession would allow
                // bucketing by "sessions", which would need to be defined in code.
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        // [END build_read_data_request]

        return readRequest;
    }

    /**
     * Log a record of the query result. It's possible to get more constrained data sets by
     * specifying a data source or data type, but for demonstrative purposes here's how one would
     * dump all the data. In this sample, logging also prints to the device screen, so we can see
     * what the query returns, but your app should not log fitness information as a privacy
     * consideration. A better option would be to dump the data you receive to a local data
     * directory to avoid exposing it to other applications.
     */
    public static void printData(DataReadResult dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: "
                    + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: "
                    + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
        // [END parse_read_data_result]
    }

    // [START parse_dataset]
    private static void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                Log.i(TAG, "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
            }
        }
    }

    


    
}
