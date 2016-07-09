package com.example.douglas.vansbroappen.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by douglas on 05/06/2016.
 */
public class StatsProvider extends ContentProvider {
    private SQLiteOpenHelper openHelper;

    interface Tables {
        String STATS = "stats";
    }

    private static final int STATS = 0;
    private static final int STATS__ID = 1;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = StatsContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, "stats", STATS);
        matcher.addURI(authority, "stats/#", STATS__ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        openHelper = new StatsDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = openHelper.getReadableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case STATS: {
                return db.query(Tables.STATS, projection, selection, selectionArgs, null, null, null);
            }
            default: {
                throw new IllegalArgumentException("Unknwon uri: " + uri);
            }
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case STATS: {
                final long _id = db.insertOrThrow(Tables.STATS, null, values);
                return StatsContract.Stats.buildItemUri(_id);
            }
            default: {
                throw new UnsupportedOperationException("Unknwon uri: " + uri);
            }
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case STATS: {
                return db.delete(Tables.STATS, selection, selectionArgs);
            }
            default: {
                throw new UnsupportedOperationException("Unknwon uri: " + uri);
            }
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case STATS: {
                return db.update(Tables.STATS, values, selection, selectionArgs);
            }
            default: {
                throw new UnsupportedOperationException("Unknwon uri: " + uri);
            }
        }
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case STATS:
                return StatsContract.Stats.CONTENT_TYPE;
            case STATS__ID:
                return StatsContract.Stats.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


}
