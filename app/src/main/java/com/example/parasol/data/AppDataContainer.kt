package com.example.parasol.data

import android.content.Context

class AppDataContainer(
    private val context: Context
) : AppContainer {
    override val citiesRepository: CitiesRepository by lazy {
        OfflineCitiesRepository(CitiesDatabase.getDatabase(context).cityDao())
    }
}