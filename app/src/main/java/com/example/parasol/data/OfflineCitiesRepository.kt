package com.example.parasol.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineCitiesRepository @Inject constructor(
        private val cityDao: CityDao // Ensure CityDao is provided via Hilt
) : CitiesRepository {
        override fun getFullListOfCities(): Flow<List<CityEntity>> = cityDao.getListOFCities()

        override fun getOneCity(id: Int): Flow<CityEntity?> = cityDao.getCity(id)

        override suspend fun insertCity(item: CityEntity) = cityDao.insert(item)

        override suspend fun deleteCity(item: CityEntity) = cityDao.delete(item)

        override suspend fun updateCity(item: CityEntity) = cityDao.update(item)
}
