package com.example.parasol.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parasol.data.CitiesRepository
import com.example.parasol.data.CityEntity
import com.example.parasol.data.UserPreferencesRepository
import com.example.parasol.network.stopLightUVI.IndexApi
import com.example.parasol.network.stopLightUVI.UvResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

sealed class IndexUiState {
    data object Loading : IndexUiState()
    data class Success(val indexes: UvResponse) : IndexUiState()
    data class Error(val message: String) : IndexUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val citiesRepository: CitiesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _indexUiState = MutableStateFlow<IndexUiState>(IndexUiState.Loading)
    val indexUiState: StateFlow<IndexUiState> = _indexUiState

    private var _selectedCityId = MutableStateFlow<Int?>(null)
    val selectedCityId: StateFlow<Int?> = _selectedCityId

    val homeUiState: StateFlow<HomeUiState> =
        citiesRepository.getFullListOfCities().map { cities ->
            if (cities.isEmpty()) {
                _indexUiState.value =
                    IndexUiState.Error("Нет доступных городов") // Устанавливаем ошибку
                HomeUiState() // Возвращаем пустое состояние HomeUiState
            } else {
                HomeUiState(cities) // Возвращаем состояние с городами
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )

    init {
        loadCities()
        viewModelScope.launch {
            userPreferencesRepository.selectedCityFlow.collect { cityId ->
                _selectedCityId.value = cityId?.toInt()
                if (_selectedCityId.value != null) {
                    loadCityCoordinatesAndGetUV()
                } else {
                    Log.w("HomeViewModel", "No city selected in DataStore.")
                    _indexUiState.value = IndexUiState.Error("Город не выбран")
                }
            }
        }
    }


    private fun loadCities() {
        viewModelScope.launch {
            citiesRepository.getFullListOfCities().collect { cities ->
                if (cities.isEmpty()) {
                    _indexUiState.value = IndexUiState.Error("Нет доступных городов")
                } else {
                    // Если в DataStore есть выбранный город, используем его
                    if (_selectedCityId.value == null) {
                        _selectedCityId.value =
                            cities.first().id // Устанавливаем первый город только если нет выбранного
                        loadCityCoordinatesAndGetUV() // Получаем UV индекс для первого города
                    }
                }
            }
        }
    }

    private fun loadCityCoordinatesAndGetUV() {
        viewModelScope.launch {
            _selectedCityId.value?.let { selectedId ->
                val cityFlow = citiesRepository.getOneCity(selectedId)

                cityFlow.collect { city ->
                    if (city != null) {
                        getUVIs(
                            city.latitude,
                            city.longitude
                        ) // Получаем UV индекс для координат города
                    } else {
                        Log.w("HomeViewModel", "City not found for ID: $selectedId")
                        _indexUiState.value =
                            IndexUiState.Error("Город не найден") // Устанавливаем ошибку
                    }
                }
            } ?: run {
                Log.w("HomeViewModel", "Selected city ID is null.")
                _indexUiState.value =
                    IndexUiState.Error("ID города не найден") // Устанавливаем ошибку
            }
        }
    }

    fun setSelectedCity(city: CityEntity?) {
        city?.let {
            if (it.id != _selectedCityId.value) {
                _selectedCityId.value = it.id
                viewModelScope.launch {
                    userPreferencesRepository.saveSelectedCity(it.id.toString())
                    getUVIs(it.latitude, it.longitude)
                }
            }
        }
    }


    private fun getUVIs(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _indexUiState.value = IndexUiState.Loading
            try {
                Log.d("HomeViewModel", "Fetching UV Index for coordinates: ($latitude, $longitude)")

                val response = IndexApi.retrofitService.getIndexes(latitude, longitude)
                _indexUiState.value = IndexUiState.Success(response)

            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun handleError(exception: Exception) {
        when (exception) {
            is IOException -> {
                Log.e("HomeViewModel", "Network error: ${exception.message}")
                _indexUiState.value = IndexUiState.Error("Ошибка сети: ${exception.message}")
            }

            is HttpException -> {
                Log.e("HomeViewModel", "HTTP error: ${exception.message}")
                _indexUiState.value = IndexUiState.Error("Ошибка HTTP: ${exception.message}")
            }

            else -> {
                Log.e("HomeViewModel", "Unexpected error: ${exception.message}")
                _indexUiState.value = IndexUiState.Error("Неожиданная ошибка: ${exception.message}")
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}




data class HomeUiState(
    val citiesList: List<CityEntity> = listOf()
)