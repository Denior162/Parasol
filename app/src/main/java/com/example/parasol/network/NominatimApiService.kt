package com.example.parasol.network

import com.example.parasol.network.model.City
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApiService {
    @GET("search")
    suspend fun searchCities(
        @Query("q") cityName: String,
        @Query("format") format: String = "json"
    ): List<City>
}