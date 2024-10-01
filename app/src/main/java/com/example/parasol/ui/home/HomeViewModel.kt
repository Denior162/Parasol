package com.example.parasol.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parasol.data.CitiesRepository
import com.example.parasol.data.CityEntity
import com.example.parasol.data.UserPreferencesRepository
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

sealed class IndexUiState {
    data object Loading : IndexUiState()
    data class Success(val indexes: UvResponse) : IndexUiState()
    data object Error : IndexUiState()
}

//@HiltViewModel
class HomeViewModel //@Inject constructor
    (
    private val citiesRepository: CitiesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _indexUiState =
        MutableStateFlow<IndexUiState>(IndexUiState.Loading)
    val indexUiState: StateFlow<IndexUiState> = _indexUiState

    private var _selectedCityId = MutableStateFlow<Int?>(null)
    val selectedCityId: StateFlow<Int?> = _selectedCityId

    val homeUiState: StateFlow<HomeUiState> =
        citiesRepository.getFullListOfCities().map { HomeUiState(it) }.stateIn(
            scope = viewModelScope, started = SharingStarted.WhileSubscribed(
                TIMEOUT_MILLIS
            ), initialValue = HomeUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    init {
        viewModelScope.launch {
            userPreferencesRepository.selectedCityFlow.collect { cityId ->
                _selectedCityId.value = cityId?.toInt()
            }
        }
    }

    fun setSelectedCity(city: CityEntity?) {
        if (city != null) {
            _selectedCityId.value = city.id
            viewModelScope.launch {
                userPreferencesRepository.saveSelectedCity(city.id.toString())
            }
            getUVIs()
        }
    }

    fun getUVIs() {
        viewModelScope.launch(Dispatchers.IO) {
            _indexUiState.value = IndexUiState.Loading
            try {
                _selectedCityId.value?.let { selectedId ->
                    val cityFlow = citiesRepository.getOneCity(selectedId)

                    cityFlow.collect { city ->
                        if (city != null) {
                            val (latitude, longitude) = Pair(city.latitude, city.longitude)
                            Log.d(
                                "HomeViewModel",
                                "Fetching UV Index for coordinates: ($latitude, $longitude)"
                            )
                            val response = IndexApi.retrofitService.getIndexes(latitude, longitude)
                            _indexUiState.value = IndexUiState.Success(response)
                        } else {
                            Log.w("HomeViewModel", "City not found for ID: $selectedId")
                            _indexUiState.value = IndexUiState.Error
                        }
                    }
                } ?: run {
                    Log.w("HomeViewModel", "Selected city ID is null.")
                    _indexUiState.value = IndexUiState.Error
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun handleError(exception: Exception) {
        when (exception) {
            is IOException -> {
                Log.e("HomeViewModel", "Network error: ${exception.message}")
                _indexUiState.value = IndexUiState.Error
            }

            is HttpException -> {
                Log.e("HomeViewModel", "HTTP error: ${exception.message}")
                _indexUiState.value = IndexUiState.Error
            }

            else -> {
                Log.e("HomeViewModel", "Unexpected error: ${exception.message}")
                _indexUiState.value = IndexUiState.Error
            }
        }
    }
}


data class HomeUiState(
    val citiesList: List<CityEntity> = listOf()
)