package com.example.opagg;

import android.app.Application;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitManager.getInstance().init();
    }
}