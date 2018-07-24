package com.baset.carfinder;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

public class ApplicationLoader extends Application {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
