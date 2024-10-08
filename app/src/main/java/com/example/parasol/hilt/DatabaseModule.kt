package com.example.parasol.hilt

import android.content.Context
import com.example.parasol.data.CitiesDatabase
import com.example.parasol.data.CityDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCityDao(database: CitiesDatabase): CityDao {
        return database.cityDao()
    }

    @Provides
    @Singleton
    fun provideCitiesDatabase(context: Context): CitiesDatabase {
        return CitiesDatabase.getDatabase(context)
    }
}

