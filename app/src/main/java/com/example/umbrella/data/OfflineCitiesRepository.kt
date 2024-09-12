package com.example.umbrella.data

import kotlinx.coroutines.flow.Flow

class OfflineCitiesRepository (private val cityDao: CityDao) : CitiesRepository {
        override fun getListOfCities(): Flow<List<CityEntity>> = cityDao.getListOFCities()

        override fun getOneCity(id: Int): Flow<CityEntity?> = cityDao.getCity(id)

        override suspend fun insertCity(item: CityEntity) = cityDao.insert(item)

        override suspend fun deleteCity(item: CityEntity) = cityDao.delete(item)

        override suspend fun updateCity(item: CityEntity) = cityDao.update(item)

}