package com.example.parasol.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.parasol.ParasolApplication
import com.example.parasol.ui.city.search.CitySearchViewModel
import com.example.parasol.ui.home.HomeViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                parasolApplication().container.citiesRepository
            )
        }
        initializer {
            CitySearchViewModel(parasolApplication().container.citiesRepository)
        }
    }
}

fun CreationExtras.parasolApplication(): ParasolApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ParasolApplication)