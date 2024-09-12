package com.example.umbrella.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.umbrella.UmbrellaApplication
import com.example.umbrella.ui.city.CityEntryVewModel
import com.example.umbrella.ui.home.HomeViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel()
        }
        initializer {
            CityEntryVewModel((umbrellaApplication().container.citiesRepository))
        }
    }
}

fun CreationExtras.umbrellaApplication(): UmbrellaApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as UmbrellaApplication)