package com.ricardo.exchangerates.components;

import com.ricardo.exchangerates.model.HttpRequester;
import com.ricardo.exchangerates.modules.HttpRequesterModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Ricardo on 27/02/2016.
 */
@Singleton @Component(modules = {HttpRequesterModule.class})

public interface HttpRequesterComponent {

    HttpRequester provideHttpRequester();
}
