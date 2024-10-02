package com.example.parasol.network.stopLightUVI

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL =
    "https://currentuvindex.com/api/v1/"

//val client = HttpClient(OkHttp) {
//    install(ContentNegotiation) {
//        json() // Используем Kotlinx для JSON-сериализации
//    }
//}

//object IndexApi {
//    suspend fun fetchUvIndex(latitude: Double, longitude: Double): UvResponse {
//        val response: HttpResponse = client.get("$BASE_URL/uvi") {
//            parameter("latitude", latitude)
//            parameter("longitude", longitude)
//        }
//
//        // Convert the HttpResponse to UvResponse
//        return response.body() // Assuming UvResponse is a data class and you have a serializer set up
//    }
//}


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

data class UvResponse(
    val latitude: Double,
    val longitude: Double,
    val now: CurrentUv,
    val forecast: List<Forecast>
)

data class CurrentUv(
    val time: String,
    val uvi: Double
)

data class Forecast(
    val time: String,
    val uvi: Double
)