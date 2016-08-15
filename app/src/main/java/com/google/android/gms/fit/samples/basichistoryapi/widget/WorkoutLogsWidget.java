package com.google.android.gms.fit.samples.basichistoryapi.widget;

import android.app.LoaderManager;
import android.appwidget.AppWidgetProvider;
import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.fit.samples.basichistoryapi.R;
import com.google.android.gms.fit.samples.basichistoryapi.data.WorkoutExerciseContract;
import com.google.android.gms.fit.samples.basichistoryapi.data.WorkoutLogContract;

/**
 * Created by archit.m on 15/08/16.
 */
public class WorkoutLogsWidget extends AppWidgetProvider {

    public static final String DATABASE_CHANGED = "utimetable.DATABASE_CHANGED";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.e("archit","Updating App");
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.collection_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);

        //context.startService(new Intent(context, WorkoutLogsIntentService.class));

        // Set up the collection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, views);
        } else {
            setRemoteAdapterV11(context, views);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {


        //context.startService(new Intent(context, WorkoutLogsIntentService.class));

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        Log.e("archit","Setting remote adapter1");
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, WidgetService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private static void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        Log.e("archit","Setting remote adapter2");
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, WidgetService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(DATABASE_CHANGED) || action.equals(Intent.ACTION_DATE_CHANGED))
        {
            Log.e("archit","Received the intent");
            AppWidgetManager gm = AppWidgetManager.getInstance(context);
            int[] ids = gm.getAppWidgetIds(new ComponentName(context, WorkoutLogsWidget.class));
            this.onUpdate(context, gm, ids);
        }
        else
        {
            super.onReceive(context, intent);
        }

    }
}
