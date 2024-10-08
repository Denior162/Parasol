package com.example.parasol.hilt

import com.example.parasol.data.CitiesRepository
import com.example.parasol.data.CityDao
import com.example.parasol.data.OfflineCitiesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCitiesRepository(cityDao: CityDao): CitiesRepository {
        return OfflineCitiesRepository(cityDao)
    }
}
