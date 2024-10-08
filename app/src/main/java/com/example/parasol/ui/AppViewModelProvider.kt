package com.example.parasol.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.parasol.ParasolApplication

//object AppViewModelProvider {
//    val Factory = viewModelFactory {
//        initializer {
//            val userPreferencesRepository = UserPreferencesRepository(parasolApplication())
//            HomeViewModel(
//                citiesRepository = parasolApplication().container.citiesRepository,
//                userPreferencesRepository = userPreferencesRepository
//            )
//        }
//        initializer {
//            CitySearchViewModel(
//                citiesRepository =,
//                geocodingApi = ge
//            )
//            //old implementation
//        // parasolApplication().container.citiesRepository)
//        }
//    }
//}

fun CreationExtras.parasolApplication(): ParasolApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ParasolApplication)