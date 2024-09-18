package com.example.umbrella.network.stopLightUVI

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL =
    "https://currentuvindex.com/api/v1/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface StopLightApiService {
    @GET("uvi")
    suspend fun getIndexes(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    ): UvResponse
}

object IndexApi {
    val retrofitService: StopLightApiService by lazy {
        retrofit.create(StopLightApiService::class.java)
    }
}