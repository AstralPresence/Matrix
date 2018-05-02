package com.apptronics.matrix;

import android.app.Application;

import com.google.firebase.FirebaseApp;

import timber.log.Timber;

/**
 * Created by Maha Perriyava on 4/30/2018.
 */

public class Matrix extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
