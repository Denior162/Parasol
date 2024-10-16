package com.example.parasol.ui.city.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parasol.data.CitiesRepository
import com.example.parasol.data.CityEntity
import com.example.parasol.network.NominatimApiService
import com.example.parasol.network.model.City
import com.example.parasol.ui.ErrorHandler.handleError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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
        if (cityName.length < minQueryLength) return

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            _cities.value = SearchUiState.Loading

            try {
                delay(debounceDelay)
                val result = geocodingApi.searchCities(cityName)

                // Filter out already added cities
                val addedCities = citiesRepository.getFullListOfCities().first().map { it.name }
                val filteredResult = result.filterNot { addedCities.contains(it.name) }

                _cities.value = if (filteredResult.isNotEmpty()) {
                    SearchUiState.Success(filteredResult)
                } else {
                    SearchUiState.Error
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
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
