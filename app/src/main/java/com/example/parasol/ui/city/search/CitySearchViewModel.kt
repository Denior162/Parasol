package com.example.parasol.ui.city.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parasol.data.CitiesRepository
import com.example.parasol.data.CityEntity
import com.example.parasol.network.geoCoding.City
import com.example.parasol.network.geoCoding.SearchCityApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SearchUiState {
    data class Success(val result: List<City>) : SearchUiState()
    data object Error : SearchUiState()
    data object Loading : SearchUiState()
}


class CitySearchViewModel(private val citiesRepository: CitiesRepository) : ViewModel() {
    private val _cities = MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val cities: StateFlow<SearchUiState> = _cities

    fun getSearchResult(cityName: String) {
        viewModelScope.launch {
            _cities.value = SearchUiState.Loading
            try {
                val result = SearchCityApi.retrofitService.searchCities(cityName)
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
}
