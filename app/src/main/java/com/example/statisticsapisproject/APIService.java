package com.example.statisticsapisproject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface APIService
{
    @GET("destinations-export")
    Call<String> getConditions();
}
