package com.ricardo.exchangerates.components;

import android.content.SharedPreferences;

import com.ricardo.exchangerates.App;
import com.ricardo.exchangerates.modules.AppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Ricardo on 27/02/2016.
 */
@Singleton @Component(modules = {AppModule.class})

public interface AppComponent {

    void provideApp(App app);

    SharedPreferences provideAppPreferences();
}
