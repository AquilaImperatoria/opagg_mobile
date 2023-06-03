package com.example.opagg;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PointsService {
    @Headers({"Accept: application/json"})
    @POST("/places/{name}")
    Call<Place> addPlace(@Path("name") String name);

    @Headers({"Accept: application/json"})
    @GET("/places")
    Call<List<Place>> getPlaces();
}
