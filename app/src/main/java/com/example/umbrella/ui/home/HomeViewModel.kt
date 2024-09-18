package com.example.umbrella.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umbrella.data.CitiesRepository
import com.example.umbrella.data.CityEntity
import com.example.umbrella.network.stopLightUVI.IndexApi
import com.example.umbrella.network.stopLightUVI.UvResponse
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
    data object Error : IndexUiState
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

    fun setSelectedCity(city: CityEntity) {
        _selectedCityId.value = city.id
    }

    private fun getSelectedCityCoordinates(): Pair<Double, Double> {
        return _selectedCityId.value?.let { selectedId ->
            homeUiState.value.citiesList.find { it.id == selectedId }
        }?.let { Pair(it.latitude, it.longitude) }
            ?: Pair(35.0, 50.0) // значения по умолчанию
    }

    init {
        getUVIs()
    }

    fun getUVIs() {
        viewModelScope.launch {
            _indexUiState.value = IndexUiState.Loading
            _indexUiState.value = try {
                val (latitude, longitude) = getSelectedCityCoordinates()
                val response = IndexApi.retrofitService.getIndexes(
                    latitude = latitude, longitude = longitude
                )
                IndexUiState.Success(response)
            } catch (e: IOException) {
                IndexUiState.Error
            } catch (e: HttpException) {
                IndexUiState.Error
            } catch (e: Exception) {
                IndexUiState.Error
            }
        }
    }
}

data class HomeUiState(
    val citiesList: List<CityEntity> = listOf()
)
