package com.example.douglas.vansbroappen.fetchData;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.douglas.vansbroappen.R;

/**
 * Created by douglas on 11/06/2016.
 */
public class StatsAdapter extends CursorAdapter {

    public StatsAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Set the textView with "name" from cursor
        TextView textView = (TextView) view;
        textView.setText(cursor.getString(LoaderUtility.Query.COMPETITION));
    }
}
