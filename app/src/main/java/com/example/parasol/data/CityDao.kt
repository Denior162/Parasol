package com.example.parasol.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: CityEntity)


    @Update
    suspend fun update(item: CityEntity)


    @Delete
    suspend fun delete(item: CityEntity)


    @Query("SELECT * from cities WHERE id = :id")
    fun getCity(id: Int): Flow<CityEntity>

    @Query("SELECT * from cities ORDER BY name ASC")
    fun getListOFCities(): Flow<List<CityEntity>>
}