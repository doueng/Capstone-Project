package com.example.douglas.vansbroappen;

import android.content.Context;

/**
 * Created by douglas on 04/06/2016.
 */
public class ContextName {

    Context context;
    String name;

    ContextName(Context context, String name) {
        this.context = context;
        this.name = name;
    }

    public Context getContext() {
        return this.context;
    }

    public String getName() {
        return this.name;
    }
}
