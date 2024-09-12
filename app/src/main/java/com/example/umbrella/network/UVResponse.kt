package com.example.umbrella.network

import kotlinx.serialization.Serializable

@Serializable
data class UvResponse(
    val ok: Boolean,
    val latitude: Double,
    val longitude: Double,
    val now: CurrentUv,
    val forecast: List<Forecast>
)

@Serializable
data class CurrentUv(
    val time: String,
    val uvi: Double
)

@Serializable
data class Forecast(
    val time: String,
    val uvi: Double
)