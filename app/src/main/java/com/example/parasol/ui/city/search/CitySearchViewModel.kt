package com.example.parasol.ui.city.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parasol.data.CitiesRepository
import com.example.parasol.data.CityEntity
import com.example.parasol.network.NominatimApiService
import com.example.parasol.network.model.City
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SearchUiState {
    data object Loading : SearchUiState()
    data class Success(val result: List<City>) : SearchUiState()
    data object Error : SearchUiState()
}

@HiltViewModel
class CitySearchViewModel @Inject constructor(
    private val citiesRepository: CitiesRepository,
    private val geocodingApi: NominatimApiService
) : ViewModel() {
    private val _cities = MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val cities: StateFlow<SearchUiState> = _cities

    private var searchJob: Job? = null
    private val minQueryLength = 3
    private val debounceDelay = 1000L // Delay in milliseconds

    fun getSearchResult(cityName: String) {
        if (cityName.length < minQueryLength) return // Ignore short queries

        searchJob?.cancel() // Cancel the previous job if it's still running

        searchJob = viewModelScope.launch {
            _cities.value = SearchUiState.Loading

            try {
                delay(debounceDelay) // Wait for the debounce period
                val result = geocodingApi.searchCities(cityName) // Use injected API service
                _cities.value = if (result.isNotEmpty()) {
                    SearchUiState.Success(result)
                } else {
                    SearchUiState.Error
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun handleError(e: Throwable) {
        Log.e("CitySearchViewModel", "Error occurred", e)
        _cities.value = SearchUiState.Error
    }

    fun saveCity(city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            val cityEntity = CityEntity(
                name = city.name,
                latitude = city.lat.toDouble(),
                longitude = city.lon.toDouble()
            )
            citiesRepository.insertCity(cityEntity)
        }
    }

    fun deleteCity(city: CityEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            citiesRepository.deleteCity(city)
        }
    }
}
