package com.example.douglas.vansbroappen.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by douglas on 05/06/2016.
 */
public class StatsDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "vansbroappen.db";
    private static final int DATABASE_VERSION = 1;

    public StatsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + StatsProvider.Tables.STATS + " ("
                + StatsContract.StatsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + StatsContract.StatsColumns.POSITION + " TEXT NOT NULL,"
                + StatsContract.StatsColumns.NAME + " TEXT NOT NULL,"
                + StatsContract.StatsColumns.CLUB + " TEXT NOT NULL,"
                + StatsContract.StatsColumns.START_NUMBER + " TEXT NOT NULL,"
                + StatsContract.StatsColumns.TIME + " TEXT NOT NULL,"
                + StatsContract.StatsColumns.SPEED + " DOUBLE NOT NULL,"
                + StatsContract.StatsColumns.COMPETITION + " TEXT NOT NULL,"
                + StatsContract.StatsColumns.DATE + " TEXT NOT NULL,"
                + StatsContract.StatsColumns.LENGTH + " TEXT NOT NULL,"
                + StatsContract.StatsColumns.NUM_STARTERS + " TEXT NOT NULL,"
                + StatsContract.StatsColumns.NUM_FINISHED + " TEXT NOT NULL,"
                + StatsContract.StatsColumns.TOP_THREE_AVERAGE_SPEED + " DOUBLE NOT NULL,"
                + StatsContract.StatsColumns.SPEED_DIFFERENCE + " DOUBLE NOT NULL,"
                + StatsContract.StatsColumns.TOP_THREE_AVERAGE_TIME + " TEXT NOT NULL,"
                + StatsContract.StatsColumns.TIME_DIFFERENCE + " TEXT NOT NULL"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StatsProvider.Tables.STATS);
    }
}
