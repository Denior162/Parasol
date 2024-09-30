package com.example.parasol.network.geoCoding

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

interface GeocodingApiSearchCity {
    @GET("search")
    suspend fun searchCities(
        @Query("q") cityName: String,
        @Query("format") format: String = "json"
    ): List<City>
}


object SearchCityApi {
    val retrofitService: GeocodingApiSearchCity by lazy {
        retrofit.create(GeocodingApiSearchCity::class.java)
    }
}