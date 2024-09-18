package com.example.umbrella.network.geoCoding

data class City(
    val address: AddressData,
    val addresstype: String,
    val display_name: String,
    val lat: String,
    val lon: String,
    val name: String,
    val category: String
)

data class AddressData(
    val borough: String,
    val city: String,
    val country: String,
    val neighbourhood: String,
    val postcode: String,
    val road: String,
    val shop: String,
    val suburb: String
)
