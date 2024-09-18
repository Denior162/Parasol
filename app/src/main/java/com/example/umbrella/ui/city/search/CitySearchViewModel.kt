package com.example.umbrella.ui.city.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umbrella.data.CitiesRepository
import com.example.umbrella.data.CityEntity
import com.example.umbrella.network.geoCoding.City
import com.example.umbrella.network.geoCoding.SearchCityApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface SearchUiState {
    data class Success(val result: List<City>) : SearchUiState // Изменено на List<City>
    data object Error : SearchUiState
    data object Loading : SearchUiState
}

class CitySearchViewModel(private val citiesRepository: CitiesRepository) : ViewModel() {
    private val _cities = MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val cities: StateFlow<SearchUiState> = _cities

    fun getSearchResult(cityName: String) {
        viewModelScope.launch {
            _cities.value = SearchUiState.Loading
            try {
                val result = SearchCityApi.retrofitService.searchCities(cityName)
                if (result.isNotEmpty()) {
                    _cities.value = SearchUiState.Success(result)
                } else {
                    _cities.value = SearchUiState.Error // Обработка пустого результата
                }
            } catch (e: IOException) {
                Log.e("CitySearchViewModel", "Network error", e)
                _cities.value = SearchUiState.Error
            } catch (e: HttpException) {
                Log.e("CitySearchViewModel", "HTTP error", e)
                _cities.value = SearchUiState.Error
            } catch (e: Exception) {
                Log.e("CitySearchViewModel", "Unexpected error", e)
                _cities.value = SearchUiState.Error
            }
        }
    }

    fun saveCity(city: City) {
        viewModelScope.launch(Dispatchers.IO) { // Запускаем в IO потоке
            val cityEntity = CityEntity(
                name = city.display_name,
                latitude = city.lat.toDouble(),
                longitude = city.lon.toDouble()
            )
            citiesRepository.insertCity(cityEntity) // Используем репозиторий для сохранения города
        }
    }
}
