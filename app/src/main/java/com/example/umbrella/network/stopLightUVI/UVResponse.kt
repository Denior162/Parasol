package com.example.umbrella.network.stopLightUVI

data class UvResponse(
    val ok: Boolean,
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