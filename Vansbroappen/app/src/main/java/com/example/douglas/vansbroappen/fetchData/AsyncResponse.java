package com.example.douglas.vansbroappen.fetchData;

import android.database.Cursor;

/**
 * Created by douglas on 04/06/2016.
 */
public interface AsyncResponse {
    void processFinish(Cursor cursor);
}
