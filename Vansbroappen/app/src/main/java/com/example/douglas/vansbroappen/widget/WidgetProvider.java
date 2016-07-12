package com.example.douglas.vansbroappen.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.example.douglas.vansbroappen.MainActivity;
import com.example.douglas.vansbroappen.R;
import com.example.douglas.vansbroappen.data.StatsContract;
import com.example.douglas.vansbroappen.fetchData.LoaderUtility;

import java.util.ArrayList;

/**
 * Created by douglas on 12/06/2016.
 */
public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {


            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                    WidgetProvider.class));

            ArrayList<String> competitions = new ArrayList<>();
            ArrayList<String> times = new ArrayList<>();
            ArrayList<String> speeds = new ArrayList<>();
            ArrayList<String> topThreeTimes = new ArrayList<>();
            ArrayList<String> topThreeSpeeds = new ArrayList<>();
            ArrayList<String> differenceTimes = new ArrayList<>();
            ArrayList<String> differenceSpeeds = new ArrayList<>();

            if (intent.hasExtra("name")) {

                String selection = StatsContract.StatsColumns.NAME + " = ?";
                String[] selectionArgs = {intent.getStringExtra("name")};
                Cursor cursor = context.getContentResolver().query(StatsContract.Stats.buildDirUri(),
                        LoaderUtility.Query.PROJECTION,
                        selection,
                        selectionArgs,
                        null);

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        competitions.add(cursor.getString(LoaderUtility.Query.COMPETITION));
                        times.add(cursor.getString(LoaderUtility.Query.TIME));
                        speeds.add(cursor.getString(LoaderUtility.Query.SPEED));
                        topThreeTimes.add(cursor.getString(LoaderUtility.Query.TOP_THREE_AVERAGE_TIME));
                        topThreeSpeeds.add(cursor.getString(LoaderUtility.Query.TOP_THREE_AVERAGE_SPEED));
                        differenceTimes.add(cursor.getString(LoaderUtility.Query.TIME_DIFFERENCE));
                        differenceSpeeds.add(cursor.getString(LoaderUtility.Query.SPEED_DIFFERENCE));
                    }
                    cursor.close();
                }
            }

            for (int appWidgetId : appWidgetIds) {
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
                remoteViews.removeAllViews(R.id.widget_container);
                for (int i = 0; i < competitions.size() - 1; i++) {
                    RemoteViews widgetTextViews = new RemoteViews(context.getPackageName(), R.layout.widget_text_views);
                    widgetTextViews.setTextViewText(R.id.widget_competition, competitions.get(i));
                    widgetTextViews.setTextViewText(R.id.time_widget, times.get(i));
                    widgetTextViews.setTextViewText(R.id.speed_widget, speeds.get(i));
                    widgetTextViews.setTextViewText(R.id.top_three_time_widget, topThreeTimes.get(i));
                    widgetTextViews.setTextViewText(R.id.top_three_speed_widget, topThreeSpeeds.get(i));
                    widgetTextViews.setTextViewText(R.id.difference_time_widget, differenceTimes.get(i));
                    widgetTextViews.setTextViewText(R.id.difference_speed_widget, differenceSpeeds.get(i));
                    remoteViews.addView(R.id.widget_container, widgetTextViews);
                }

                Intent launchIntent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
                remoteViews.setOnClickPendingIntent(R.id.widget_container, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }
        }
    }

}
