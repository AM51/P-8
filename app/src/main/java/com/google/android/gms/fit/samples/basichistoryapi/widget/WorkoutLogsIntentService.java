package com.google.android.gms.fit.samples.basichistoryapi.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fit.samples.basichistoryapi.R;
import com.google.android.gms.fit.samples.basichistoryapi.WorkoutHistoryHelper;
import com.google.android.gms.fit.samples.basichistoryapi.WorkoutLog;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.fitness.data.Field.FIELD_EXERCISE;
import static com.google.android.gms.fitness.data.Field.FIELD_REPETITIONS;
import static com.google.android.gms.fitness.data.Field.FIELD_RESISTANCE;
import static java.text.DateFormat.getTimeInstance;

/**
 * Created by archit.m on 15/08/16.
 */
public class WorkoutLogsIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public static GoogleApiClient mClient = null;
    public static final String TAG = "TodaysWorkoutLogActivity";


    public WorkoutLogsIntentService(){
        super("WorkoutLogsIntentService");
    }

    public WorkoutLogsIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.e("widgetTest","Inside intent service");
        int layoutId = R.layout.collection_widget;
        RemoteViews views = new RemoteViews(getPackageName(), layoutId);
        views.setTextViewText(R.id.widget_header,"Changed Header");


        buildFitnessClient();
//        DataSet dataSet = insertWorkoutData(new WorkoutLog("abc", 10, 10.0f));
//        com.google.android.gms.common.api.Status insertStatus =
//                Fitness.HistoryApi.insertData(mClient, dataSet)
//                        .await(2, TimeUnit.MINUTES);
//
//        // Before querying the data, check to see if the insertion succeeded.
//        if (!insertStatus.isSuccess()) {
//            Log.e("widgetTest", "There was a problem inserting the dataset.");
//            Log.e("widgetTest",insertStatus.getStatusMessage());
//        } else {
//            Log.e("widgetTest","Insert Successful");
//        }


        List<WorkoutLog> logs = f();
        Log.e("widgetTest",""+logs.size());

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                WorkoutLogsWidget.class));

        for (int appId : appWidgetIds) {
            Log.e("widgetTest","Updating widget");
            appWidgetManager.updateAppWidget(appId, views);
        }


    }

    private List<WorkoutLog> f(){
        DataReadRequest readRequest = WorkoutHistoryHelper.queryWorkoutData(1471199400000L);
        DataReadResult dataReadResult =
                Fitness.HistoryApi.readData(mClient, readRequest).await(2, TimeUnit.MINUTES);
        Log.e("widgetTest","result fetched: "+dataReadResult.getBuckets().size()+" "+dataReadResult.getDataSets().size());

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
        return null;
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
                .build();
    }

    private List<WorkoutLog> parseDataset(DataSet dataSet){
        List<WorkoutLog> workoutLogs = new ArrayList<>();
        Log.e("widgetTest",""+dataSet.getDataPoints().size());
        for (DataPoint dp : dataSet.getDataPoints()) {
            WorkoutLog workoutLog = new WorkoutLog();
            for(Field field : dp.getDataType().getFields()) {
                String fieldName = field.getName();
                Value dpValue = dp.getValue(field);
                Log.e("widgetTest",fieldName+" "+dpValue.toString());
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

    private DataSet insertWorkoutData(WorkoutLog workoutLog){

        com.google.android.gms.fit.samples.common.logger.Log.i(TAG, "Creating a new data insert request for workout.");

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
