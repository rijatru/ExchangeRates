package com.ricardo.exchangerates;

import android.app.Application;
import android.content.Context;

import com.ricardo.exchangerates.components.AppComponent;
import com.ricardo.exchangerates.components.DaggerAppComponent;
import com.ricardo.exchangerates.modules.AppModule;

/**
 * Created by Ricardo on 27/02/2016.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        createDaggerInjections();
    }

    public static App getAppInstance(Context context) {

        return (App) context.getApplicationContext();
    }

    private void createDaggerInjections() {

        AppComponent appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        appComponent.provideApp(this);
    }
}
