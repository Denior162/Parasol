package com.example.parasol.network.geoCoding

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://nominatim.openstreetmap.org/"

private val client = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val originalRequest = chain.request()
        val requestWithUserAgent = originalRequest.newBuilder()
            .header("User-Agent", "YourAppName/1.0") // Укажите имя вашего приложения
            .build()
        chain.proceed(requestWithUserAgent)
    }
    .build()

private val retrofit = Retrofit.Builder()
    .client(client) // Используйте настроенный клиент
    .addConverterFactory(GsonConverterFactory.create())
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
