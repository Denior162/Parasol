package com.example.parasol.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CityEntity::class], version = 1, exportSchema = false)
abstract class CitiesDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao

    companion object {
        @Volatile
        private var Instance: CitiesDatabase? = null
        fun getDatabase(context: Context): CitiesDatabase {
            return Instance ?: synchronized(this) {
                return Instance ?: synchronized(this) {
                    Room.databaseBuilder(context, CitiesDatabase::class.java, "city_database")
                        .build()
                        .also { Instance = it }
                }
            }
        }
    }
}
