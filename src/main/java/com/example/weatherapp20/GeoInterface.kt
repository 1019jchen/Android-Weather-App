package com.example.weatherapp20

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GeoInterface {
    @GET("/geo/1.0/direct")
    suspend fun fetchData(@Query("q") city:String,
                          @Query("limit") limit: Int,
                          @Query("appid") appid: String
    ): List<GeoModel>
}