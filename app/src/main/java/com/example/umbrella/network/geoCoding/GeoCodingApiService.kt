package com.example.umbrella.network.geoCoding

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL =
    "https://nominatim.openstreetmap.org/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(
        GsonConverterFactory.create()
    )
    .baseUrl(BASE_URL)
    .build()

interface GeocodingApi {
    @GET("search")
    suspend fun searchCities(
        @Query("q") cityName: String,
        @Query("format") format: String = "json"
    ): List<City>
}

object SearchCityApi {
    val retrofitService: GeocodingApi by lazy {
        retrofit.create(GeocodingApi::class.java)
    }
}