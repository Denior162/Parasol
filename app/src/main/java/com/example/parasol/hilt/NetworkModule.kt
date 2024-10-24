package com.example.parasol.hilt

import com.example.parasol.network.CurrentUvApiService
import com.example.parasol.network.NominatimReverseApiService
import com.example.parasol.network.NominatimSearchApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val GEOCODING_BASE_URL = "https://nominatim.openstreetmap.org/"
    private const val UV_INDEX_BASE_URL = "https://currentuvindex.com/api/v1/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestWithUserAgent = chain.request().newBuilder()
                    .header("User-Agent", "YourAppName/1.0")
                    .build()
                chain.proceed(requestWithUserAgent)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun provideGeocodingSearchApiService(
        client: OkHttpClient,
        gson: Gson
    ): NominatimSearchApiService {
        val retrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create(gson)
            )
            .baseUrl(GEOCODING_BASE_URL)
            .build()

        return retrofit.create(NominatimSearchApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeocodingReverseApiService(
        client: OkHttpClient,
        gson: Gson
    ): NominatimReverseApiService {
        val retrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create(gson)
            )
            .baseUrl(GEOCODING_BASE_URL)
            .build()

        return retrofit.create(NominatimReverseApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideStopLightApiService(client: OkHttpClient, gson: Gson): CurrentUvApiService {
        val retrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create(gson)
            )
            .baseUrl(UV_INDEX_BASE_URL)
            .build()

        return retrofit.create(CurrentUvApiService::class.java)
    }
}
