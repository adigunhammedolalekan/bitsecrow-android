package com.bitsescrow.app.bitsescrow.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bitsescrow.app.bitsescrow.App;
/**
 * Created by Lekan Adigun on 3/26/2018.
 */

public class RepositoryManager {

    private SharedPreferences mSharedPreferences;
    private static RepositoryManager manager;

    private RepositoryManager() {
        Context context = App.getApp().getApplicationContext();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static synchronized RepositoryManager manager() {

        if (manager == null)
            manager = new RepositoryManager();
        return manager;
    }

    public SharedPreferences preferences() {
        return mSharedPreferences;
    }
}
