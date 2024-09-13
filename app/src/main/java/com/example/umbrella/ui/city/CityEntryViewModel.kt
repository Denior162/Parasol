package com.example.umbrella.ui.city

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.umbrella.data.CitiesRepository
import com.example.umbrella.data.CityEntity

class CityEntryViewModel(private val citiesRepository: CitiesRepository) : ViewModel() {
    var cityUiState by mutableStateOf(CityUiState())
        private set

    fun updateUiState(cityDetails: CityDetails) {
        cityUiState =
            CityUiState(cityDetails = cityDetails, isEntryValid = validateInput(cityDetails))
    }

    private fun validateInput(uiState: CityDetails = cityUiState.cityDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && latitude
                .isNotBlank()
                    && longitude
                    .isNotBlank()
        }
    }
    suspend fun saveCity() {
        if (validateInput()) {
            citiesRepository.insertCity(cityUiState.cityDetails.toCity())
        }

    }
}

data class CityUiState(
    val cityDetails: CityDetails = CityDetails(),
    val isEntryValid: Boolean = false
)

data class CityDetails(
    val id: Int = 0,
    val name: String = "",
    val latitude: String = "",
    val longitude: String = "" ,
)

fun CityDetails.toCity(): CityEntity = CityEntity(
    id = id,
    name = name,
    latitude = latitude.toDoubleOrNull() ?: 0.0,
    longitude = longitude.toDoubleOrNull() ?: 0.0
)

fun CityEntity.toCityUiState(isEntryValid: Boolean = false): CityUiState = CityUiState(
    cityDetails = this.toCityDetails(),
    isEntryValid = isEntryValid
)

fun CityEntity.toCityDetails(): CityDetails = CityDetails(
    id = id,
    name = name,
    latitude = latitude.toString(),
    longitude = longitude.toString()
)