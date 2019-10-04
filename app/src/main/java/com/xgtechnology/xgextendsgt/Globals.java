package com.xgtechnology.xgextendsgt;

/**
 * Created by timr on 11/1/2017.
 */

import android.content.Context;
public class Globals {

    private static Context context;

    public static Context getContext()
    {
        return context;
    }

    public static void setContext(Context context)
    {
        Globals.context = context;
    }
}
