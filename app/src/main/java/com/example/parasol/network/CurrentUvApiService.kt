package com.example.parasol.network

import com.example.parasol.network.model.UvResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrentUvApiService {
    @GET("uvi")
    suspend fun getIndexes(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    ): UvResponse
}