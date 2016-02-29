package com.ricardo.exchangerates.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.ricardo.exchangerates.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ricardo on 27/02/2016.
 */
@Module
public class AppModule {

    Application app;

    public AppModule(Application application) {

        app = application;
    }

    @Provides @Singleton
    Application providesApplication() {

        return app;
    }

    @Provides @Singleton
    public SharedPreferences getAppPreferences() {

        return app.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
    }
}
