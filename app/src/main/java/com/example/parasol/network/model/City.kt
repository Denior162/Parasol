package com.example.parasol.network.model

import com.google.gson.annotations.SerializedName

data class City(
    val address: AddressData,
    @SerializedName("display_name")
    val displayName: String,  // Use camelCase for Kotlin
    val lat: String,
    val lon: String,
    val name: String,
)

data class AddressData(
    val city: String,
    val country: String
)