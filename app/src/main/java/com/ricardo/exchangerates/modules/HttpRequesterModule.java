package com.ricardo.exchangerates.modules;

import com.ricardo.exchangerates.model.HttpRequester;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ricardo on 27/02/2016.
 */
@Module
public class HttpRequesterModule {

    @Provides @Singleton
    HttpRequester provideHttpRequester(){

        return new HttpRequester();
    }
}
