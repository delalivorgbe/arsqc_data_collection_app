package com.ashesi.delalivorgbe.arsqc;

import android.app.Application;
import android.content.Context;

/**
 * Created by delalivorgbe on 11/6/15.
 */
public class ApplicationContextProvider extends Application {
    /**
     * Keeps a reference of the application context
     */
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();

    }

    /**
     * Returns the application context
     *
     * @return application context
     */
    public static Context getContext() {
        return sContext;
    }
}
