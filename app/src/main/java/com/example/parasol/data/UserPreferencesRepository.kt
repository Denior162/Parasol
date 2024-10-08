package com.example.parasol.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val USER_PREFERENCES_NAME = "user_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES_NAME)

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val context: Context
) {

    private object PreferencesKeys {
        val SELECTED_CITY = stringPreferencesKey("selected_city")
    }

    val selectedCityFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_CITY]
        }

    suspend fun saveSelectedCity(cityId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_CITY] = cityId
        }
    }
}
