package com.google.android.gms.fit.samples.basichistoryapi.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by archit.m on 15/08/16.
 */
public class WorkoutLogsProvider extends ContentProvider {

    private WorkoutLogDbHelper workoutLogDbHelper;
    private final static UriMatcher uriMatcher = buildUriMatcher();
    private final static int EXERCISE = 100;

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = WorkoutLogContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, WorkoutLogContract.PATH_WORKOUT_LOGS, EXERCISE);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        workoutLogDbHelper = new WorkoutLogDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor cursor = null;
        switch (uriMatcher.match(uri)){
            case EXERCISE: {
                cursor = workoutLogDbHelper.getReadableDatabase().query(WorkoutLogContract.WLog.TABLE_NAME,projection, selection,selectionArgs,null,null,sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Not a valid URI");
        }
        if( cursor != null)cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        int match = uriMatcher.match(uri);
        switch (match){
            case EXERCISE:
                return WorkoutLogContract.WLog.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Not a valid URI");
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri returnUri;
        switch (uriMatcher.match(uri)){
            case EXERCISE: {
                long id = workoutLogDbHelper.getWritableDatabase().insert(WorkoutLogContract.WLog.TABLE_NAME,null,values);
                if(id > 0){
                    returnUri= WorkoutLogContract.WLog.buildUri();
                } else {
                    throw new android.database.SQLException("Unable to insert into db for uri = "+uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Not a valid URI");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int rowsDeleted;
        switch (uriMatcher.match(uri)){
            case EXERCISE:
                rowsDeleted = workoutLogDbHelper.getWritableDatabase().delete(WorkoutLogContract.WLog.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not a valid URI");
        }
        if(rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

}
