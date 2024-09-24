package com.example.parasol.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parasol.data.CitiesRepository
import com.example.parasol.data.CityEntity
import com.example.parasol.network.stopLightUVI.IndexApi
import com.example.parasol.network.stopLightUVI.UvResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface IndexUiState {
    data object Loading : IndexUiState
    data class Success(val indexes: UvResponse) : IndexUiState
    data class Error(val message: String) : IndexUiState
}


class HomeViewModel(citiesRepository: CitiesRepository) : ViewModel() {
    private val _indexUiState = MutableStateFlow<IndexUiState>(IndexUiState.Loading)
    val indexUiState: StateFlow<IndexUiState> = _indexUiState

    val homeUiState: StateFlow<HomeUiState> =
        citiesRepository.getFullListOfCities().map { HomeUiState(it) }.stateIn(
            scope = viewModelScope, started = SharingStarted.WhileSubscribed(
                TIMEOUT_MILLIS
            ), initialValue = HomeUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private var _selectedCityId = MutableStateFlow<Int?>(null)
    val selectedCityId: StateFlow<Int?> = _selectedCityId

    fun setSelectedCity(city: CityEntity?) {
        if (city != null) {
            _selectedCityId.value = city.id
            Log.d("HomeViewModel", "Selected city ID set to: ${_selectedCityId.value}")
            getUVIs()
        } else {
            Log.d("HomeViewModel", "Attempted to set selected city to null")
        }
    }

    private fun getDefaultCityCoordinates(): Pair<Double, Double> {
        return homeUiState.value.citiesList.firstOrNull()?.let {
            Pair(it.latitude, it.longitude)
        } ?: Pair(50.0, 36.10) // Фиксированные координаты
    }

    private fun getSelectedCityCoordinates(): Pair<Double, Double> {
        val selectedId = _selectedCityId.value
        Log.d("HomeViewModel", "Current selected city ID: $selectedId")

        return if (selectedId != null) {
            homeUiState.value.citiesList.find { it.id == selectedId }?.let {
                Log.d(
                    "HomeViewModel",
                    "Coordinates for city ID $selectedId: (${it.latitude}, ${it.longitude})"
                )
                Pair(it.latitude, it.longitude)
            } ?: getDefaultCityCoordinates()
        } else {
            Log.w("HomeViewModel", "Selected city ID is null. Using default coordinates.")
            getDefaultCityCoordinates()
        }
    }

    init {
        getUVIs()
    }

    fun getUVIs() {
        viewModelScope.launch(Dispatchers.IO) {
            _indexUiState.value = IndexUiState.Loading
            try {
                val (latitude, longitude) = getSelectedCityCoordinates()
                Log.d("HomeViewModel", "Fetching UV Index for coordinates: ($latitude, $longitude)")
                val response = IndexApi.retrofitService.getIndexes(latitude, longitude)
                _indexUiState.value = IndexUiState.Success(response)
            } catch (e: IOException) {
                Log.e("HomeViewModel", "Network error: ${e.message}")
                _indexUiState.value = IndexUiState.Error("Network error: ${e.message}")
            } catch (e: HttpException) {
                Log.e("HomeViewModel", "HTTP error: ${e.message}")
                _indexUiState.value = IndexUiState.Error("HTTP error: ${e.message}")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Unexpected error: ${e.message}")
                _indexUiState.value = IndexUiState.Error("Unexpected error: ${e.message}")
            }

        }
    }
}

data class HomeUiState(
    val citiesList: List<CityEntity> = listOf()
)