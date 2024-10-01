//package com.example.parasol.data
//
//import android.content.Context
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object RepositoryModule {
//
//    @Provides
//    @Singleton
//    fun provideCitiesDatabase(context: Context): CitiesDatabase {
//        return CitiesDatabase.getDatabase(context)
//    }
//
//    @Provides
//    @Singleton
//    fun provideCityDao(database: CitiesDatabase): CityDao {
//        return database.cityDao()
//    }
//
//    @Provides
//    @Singleton
//    fun provideCitiesRepository(cityDao: CityDao): CitiesRepository {
//        return OfflineCitiesRepository(cityDao)
//    }
//
//    @Provides
//    @Singleton
//    fun provideUserPreferencesRepository(context: Context): UserPreferencesRepository {
//        return UserPreferencesRepository(context)
//    }
//}
//
//
