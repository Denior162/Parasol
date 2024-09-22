package com.example.umbrella.network.geoCoding

data class City(
    val address: AddressData,
    val display_name: String,
    val lat: String,
    val lon: String,
    val name: String,
)

data class AddressData(
    val city: String,
    val country: String
)
