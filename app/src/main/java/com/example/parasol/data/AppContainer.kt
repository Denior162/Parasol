package com.example.parasol.data

import android.content.Context

interface AppContainer {
    val citiesRepository: CitiesRepository
}

class AppDataContainer //@Inject constructor
    (
    private val context: Context
) : AppContainer {
    override val citiesRepository: CitiesRepository by lazy {
        OfflineCitiesRepository(CitiesDatabase.getDatabase(context).cityDao())
    }
}
