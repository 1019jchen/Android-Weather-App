package com.example.weatherapp20

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherInterface {
    @GET("/data/2.5/weather")
    suspend fun fetchData(@Query("lat") lat: Double?,
                          @Query("lon") lon: Double?,
                          @Query("appid") appid: String,
                          @Query("units") units: String
    ): WeatherModel
}