package com.example.parasol.ui.citySearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parasol.data.CitiesRepository
import com.example.parasol.data.CityEntity
import com.example.parasol.network.NominatimApiService
import com.example.parasol.network.model.City
import com.example.parasol.ui.home.HomeUiState
import com.example.parasol.utils.ErrorHandler.handleError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    private val _citySearchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val citySearchUiState: StateFlow<SearchUiState> = _citySearchUiState


    private var searchJob: Job? = null
    private val minQueryLength = 3
    private val debounceDelay = 1000L // Delay in milliseconds


    val homeUiState: StateFlow<HomeUiState> =
        citiesRepository.getFullListOfCities().map { cities ->
            HomeUiState(cities) // Return state with available cities
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeUiState()
        )


    fun searchCityByName(cityName: String) {
        if (cityName.length < minQueryLength) return

        searchJob?.cancel()

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            _citySearchUiState.value = SearchUiState.Loading

            try {
                delay(debounceDelay)
                val result = geocodingApi.searchCities(cityName)

                // Filter out already added cities
                val addedCities = citiesRepository.getFullListOfCities().first().map { it.name }
                val filteredResult = result.filterNot { addedCities.contains(it.name) }

                _citySearchUiState.value = if (filteredResult.isNotEmpty()) {
                    SearchUiState.Success(filteredResult)
                } else {
                    SearchUiState.Error
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun addCityToRepository(city: City) {
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
