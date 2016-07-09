package com.example.douglas.vansbroappen.fetchData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.douglas.vansbroappen.ContextName;
import com.example.douglas.vansbroappen.Utility;
import com.example.douglas.vansbroappen.data.StatsContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by douglas on 31/05/2016.
 */
public class FetchJsonData extends AsyncTask<ContextName, Void, Cursor> {

    public AsyncResponse delegate = null;
    private Cursor cursor;

    @Override
    protected Cursor doInBackground(ContextName... params) {

        Context context = params[0].getContext();
        String name = params[0].getName();


        // Check if the name is already in the database, and if it is return cursor
        if (checkIfNameExistsInDatabase(name, context)) {
            return cursor;
        }

        // Add competitions that the person has competed in from the JSON-files
        // to the SQL-database
        try {
            String[] jsonFileNames = context.getAssets().list("JsonFiles");
            for (String jsonFile : jsonFileNames) {
                loadJSONFromAsset(name, context, jsonFile);
            }
        } catch (IOException e) {
            Log.e("FetchJsonData", e.getMessage());
        }


        // Check if name is in database, if not return null and show a toast
        if (checkIfNameExistsInDatabase(name, context)) {
            return cursor;
        }
        cursor.close();

        return null;
    }

    public boolean checkIfNameExistsInDatabase(String name, Context context) {


        String selection = StatsContract.StatsColumns.NAME + " = ?";
        String[] selectionArgs = {name};

        cursor = context.getContentResolver().query(StatsContract.Stats.buildDirUri(),
                LoaderUtility.Query.PROJECTION, selection, selectionArgs, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (cursor.getString(LoaderUtility.Query.NAME).equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void loadJSONFromAsset(String name, Context context, String jsonFile) {

        String json = null;
        BufferedReader reader = null;

        try {

            InputStream inputStream = context.getAssets().open("JsonFiles/" + jsonFile);
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return;
            }
            json = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            return;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        try {
            getDataFromJson(json, name, context);
        } catch (JSONException e) {
            Log.e("FETCH", e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public void getDataFromJson(String json, String name, Context context) throws JSONException {
        final String POSITION = "Placering";
        final String NAME = "Namn";
        final String CLUB = "Klubb/Ort";
        final String START_NUMBER = "Startnr";
        final String TIME = "Tid";
        final String COMPETITION = "Tävling";
        final String DATE = "Datum";
        final String LENGTH = "Sträcka";
        final String NUM_STARTERS = "Start";
        final String NUM_FINISHED = "I mål";

        JSONArray array = new JSONArray(json);

        for (int i = 1; i < array.length(); i++) {
            JSONObject person = array.getJSONObject(i);
            String jsonName = person.getString(NAME);
            if (jsonName.equals(name)) {

                JSONObject stats = array.getJSONObject(0);
                JSONObject firstPlace = array.getJSONObject(1);
                JSONObject secondPlace = array.getJSONObject(2);
                JSONObject thirdPlace = array.getJSONObject(3);

                int lapLength = Integer.parseInt(stats.getString(LENGTH));

                double topThreeAverageSpeed = (convertSpeedToKmPerH(lapLength, firstPlace.getString(TIME))
                        + convertSpeedToKmPerH(lapLength, secondPlace.getString(TIME))
                        + convertSpeedToKmPerH(lapLength, thirdPlace.getString(TIME))) / 3;
                double topThreeAverageTime = (convertTimeToSec(firstPlace.getString(TIME))
                        + convertTimeToSec(secondPlace.getString(TIME))
                        + convertTimeToSec(thirdPlace.getString(TIME))) / 3;

                String time = person.getString(TIME);
                double timeSec = convertTimeToSec(time);
                double speed = convertSpeedToKmPerH(lapLength, time);

                double speedDifference = Math.round((topThreeAverageSpeed - speed) * 10.0) / 10.0;
                double timeSecDifference = Math.round((topThreeAverageTime - timeSec) * 10.0) / 10.0;

                if (timeSecDifference < 0) {
                    timeSecDifference = timeSecDifference * -1;
                }

                Utility utility = new Utility();
                String timeDifference = utility.convertTimeToString(timeSecDifference);
                String topThreeTimeString = utility.convertTimeToString(topThreeAverageTime);

                ContentValues values = new ContentValues();

                values.put(StatsContract.Stats.POSITION, person.getString(POSITION));
                values.put(StatsContract.Stats.NAME, name);
                values.put(StatsContract.Stats.CLUB, person.getString(CLUB));
                values.put(StatsContract.Stats.START_NUMBER, person.getString(START_NUMBER));
                values.put(StatsContract.Stats.TIME, time);
                values.put(StatsContract.Stats.SPEED, speed);
                values.put(StatsContract.Stats.COMPETITION, stats.getString(COMPETITION));
                values.put(StatsContract.Stats.DATE, stats.getString(DATE));
                values.put(StatsContract.Stats.LENGTH, stats.getString(LENGTH));
                values.put(StatsContract.Stats.NUM_STARTERS, stats.getString(NUM_STARTERS));
                values.put(StatsContract.Stats.NUM_FINISHED, stats.getString(NUM_FINISHED));
                values.put(StatsContract.Stats.TOP_THREE_AVERAGE_SPEED, topThreeAverageSpeed);
                values.put(StatsContract.Stats.SPEED_DIFFERENCE, speedDifference);
                values.put(StatsContract.Stats.TOP_THREE_AVERAGE_TIME, topThreeTimeString);
                values.put(StatsContract.Stats.TIME_DIFFERENCE, timeDifference);

                context.getContentResolver().insert(StatsContract.Stats.buildDirUri(), values);
            }
        }
    }

    public double convertSpeedToKmPerH(int lapLength, String time) {
        double result;
        String[] parts = time.split(":");

        if (parts.length == 3) {
            int hours = Integer.parseInt(parts[0]);
            int min = hours * 60 + Integer.parseInt(parts[1]);
            double sec = min * 60 + Double.parseDouble(parts[2]);
            double mPerMin = lapLength / sec;
            result = mPerMin * 3.6;
        } else {
            int min = Integer.parseInt(parts[0]);
            double sec = min * 60 + Double.parseDouble(parts[1]);
            double mPerSec = lapLength / sec;
            result = mPerSec * 3.6;
        }

        result = Math.round(result * 10.0) / 10.0;

        return result;
    }

    public double convertTimeToSec(String time) {
        double result;
        String[] parts = time.split(":");

        if (parts.length == 3) {
            int hours = Integer.parseInt(parts[0]);
            int min = hours * 60 + Integer.parseInt(parts[1]);
            double sec = Double.parseDouble(parts[2]);
            result = min * 60 + sec;

        } else {
            int min = Integer.parseInt(parts[0]);
            result = min * 60 + Double.parseDouble(parts[1]);
        }
        return result;
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        delegate.processFinish(cursor);
    }
}

