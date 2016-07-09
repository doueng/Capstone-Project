package com.example.douglas.vansbroappen.data;

import android.net.Uri;

/**
 * Created by douglas on 05/06/2016.
 */
public class StatsContract {
    public static final String CONTENT_AUTHORITY = "com.example.douglas.vansbroappen";
    public static final Uri BASE_URI = Uri.parse("content://com.example.douglas.vansbroappen");

    public interface StatsColumns {
        String _ID = "_id";
        String POSITION = "position";
        String NAME = "name";
        String CLUB = "club";
        String START_NUMBER = "startNumber";
        String TIME = "time";
        String SPEED = "speed";
        String COMPETITION = "competition";
        String DATE = "date";
        String LENGTH = "length";
        String NUM_STARTERS = "numStarters";
        String NUM_FINISHED = "numFinished";
        String TOP_THREE_AVERAGE_SPEED = "topThreeAverageSpeed";
        String SPEED_DIFFERENCE = "speedDifference";
        String TOP_THREE_AVERAGE_TIME = "topThreeAverageTime";
        String TIME_DIFFERENCE = "timeDifference";
    }

    public static class Stats implements StatsColumns {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.example.douglas.vansbroappen";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.example.douglas.vansbroappen.items";

        public static final String DEFAULT_SORT = _ID + " DESC";

        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath("stats").build();
        }

        public static Uri buildItemUri(long _id) {
            return BASE_URI.buildUpon().appendPath("stats").appendPath(Long.toString(_id)).build();
        }

        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }
    }


}
