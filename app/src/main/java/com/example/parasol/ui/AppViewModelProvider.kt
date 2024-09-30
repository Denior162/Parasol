package com.example.parasol.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.parasol.ParasolApplication
import com.example.parasol.data.UserPreferencesRepository
import com.example.parasol.ui.city.search.CitySearchViewModel
import com.example.parasol.ui.home.HomeViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val userPreferencesRepository = UserPreferencesRepository(parasolApplication())
            HomeViewModel(
                citiesRepository = parasolApplication().container.citiesRepository,
                userPreferencesRepository = userPreferencesRepository
            )
        }
        initializer {
            CitySearchViewModel(parasolApplication().container.citiesRepository)
        }
    }
}

fun CreationExtras.parasolApplication(): ParasolApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ParasolApplication)