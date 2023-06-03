package com.example.opagg;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    private Retrofit retrofit;

    private PointsService pointsService;
    private static RetrofitManager instance;

    public static RetrofitManager getInstance() {
        if (instance == null) {
            instance = new RetrofitManager();
        }
        return instance;
    }

    public void init() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.60.131:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        pointsService = retrofit.create(PointsService.class);
    }

    public PointsService getPointsService() {
        return pointsService;
    }
}