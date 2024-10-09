package com.example.parasol.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parasol.data.CitiesRepository
import com.example.parasol.data.CityEntity
import com.example.parasol.data.UserPreferencesRepository
import com.example.parasol.network.CurrentUvApiService
import com.example.parasol.network.model.UvResponse
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

/**
 * Represents the UI state for the UV index.
 */
sealed class IndexUiState {
    data object Loading : IndexUiState()
    data class Success(val indexes: UvResponse) : IndexUiState()
    data class Error(val errorMessage: String) : IndexUiState()
}

/**
 * ViewModel for managing home-related data and UI state.
 *
 * @property citiesRepository Repository for city data.
 * @property userPreferencesRepository Repository for user preferences.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val uvIndexApi: CurrentUvApiService,
    private val citiesRepository: CitiesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _indexUiState = MutableStateFlow<IndexUiState>(IndexUiState.Loading)
    val indexUiState: StateFlow<IndexUiState> = _indexUiState

    private var _selectedCityId = MutableStateFlow<Int?>(null)
    val selectedCityId: StateFlow<Int?> = _selectedCityId

    /**
     * The current state of the home UI, which includes a list of cities.
     */
    val homeUiState: StateFlow<HomeUiState> =
        citiesRepository.getFullListOfCities().map { cities ->
            if (cities.isEmpty()) {
                _indexUiState.value = IndexUiState.Error(NO_CITIES_AVAILABLE)
                HomeUiState() // Return empty state if no cities are available
            } else {
                HomeUiState(cities) // Return state with available cities
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )

    init {
        loadCities()
        observeSelectedCity()
    }

    fun retryAction() {
        _selectedCityId.value?.let { selectedId ->
            viewModelScope.launch {
                val cityFlow = citiesRepository.getOneCity(selectedId)
                cityFlow.collect { city ->
                    if (city != null) {
                        getUVIs(latitude = city.latitude, longitude = city.longitude)
                    } else {
                        handleError(Exception("City not found for ID: $selectedId"))
                    }
                }
            }
        } ?: run {
            handleError(Exception("Selected city ID is null."))
        }
    }

    /**
     * Loads cities from the repository and updates the selected city ID if necessary.
     */
    private fun loadCities() {
        viewModelScope.launch {
            citiesRepository.getFullListOfCities().collect { cities ->
                if (cities.isEmpty()) {
                    _indexUiState.value = IndexUiState.Error(NO_CITIES_AVAILABLE)
                } else if (_selectedCityId.value == null) {
                    _selectedCityId.value = cities.first().id // Set first city if none selected
                    loadCityCoordinatesAndGetUV() // Fetch UV index for first city
                }
            }
        }
    }

    /**
     * Observes changes to the selected city from user preferences.
     */
    private fun observeSelectedCity() {
        viewModelScope.launch {
            userPreferencesRepository.selectedCityFlow.collect { cityId ->
                _selectedCityId.value = cityId?.toInt()
                if (_selectedCityId.value != null) {
                    loadCityCoordinatesAndGetUV()
                } else {
                    Log.w("HomeViewModel", "No city selected in DataStore.")
                    _indexUiState.value = IndexUiState.Error(CITY_NOT_SELECTED)
                }
            }
        }
    }

    /**
     * Loads coordinates for the selected city and fetches the UV index.
     */
    private fun loadCityCoordinatesAndGetUV() {
        viewModelScope.launch {
            _selectedCityId.value?.let { selectedId ->
                val cityFlow = citiesRepository.getOneCity(selectedId)

                cityFlow.collect { city ->
                    if (city != null) {
                        getUVIs(city.latitude, city.longitude)
                    } else {
                        Log.w("HomeViewModel", "City not found for ID: $selectedId")
                        _indexUiState.value = IndexUiState.Error(CITY_NOT_FOUND)
                    }
                }
            } ?: run {
                Log.w("HomeViewModel", "Selected city ID is null.")
                _indexUiState.value = IndexUiState.Error(CITY_ID_NOT_FOUND)
            }
        }
    }

    /**
     * Sets the currently selected city and saves it to user preferences.
     *
     * @param city The selected CityEntity.
     */
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

    /**
     * Fetches UV index values based on latitude and longitude.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     */
    private fun getUVIs(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _indexUiState.value = IndexUiState.Loading
            try {
                Log.d("HomeViewModel", "Fetching UV Index for coordinates: ($latitude, $longitude)")
                val response = uvIndexApi.getIndexes(latitude, longitude)
                _indexUiState.value = IndexUiState.Success(response)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    /**
     * Handles errors that occur during API calls.
     *
     * @param exception The exception thrown during API call.
     */
    private fun handleError(exception: Exception) {
        val errorMessage = when (exception) {
            is IOException -> "Network error: ${exception.message}"
            is HttpException -> "HTTP error: ${exception.message}"
            else -> "Unexpected error: ${exception.message}"
        }
        Log.e("ViewModelError", errorMessage)
        _indexUiState.value = IndexUiState.Error(errorMessage)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

        // Constants for error messages to avoid hardcoded strings in code.
        private const val NO_CITIES_AVAILABLE = "No cities available"
        private const val CITY_NOT_SELECTED = "City not selected"
        private const val CITY_NOT_FOUND = "City not found"
        private const val CITY_ID_NOT_FOUND = "City ID not found"
    }

}

/**
 * Represents the UI state for home, including a list of cities.
 *
 * @property citiesList A list of available CityEntity objects.
 */
data class HomeUiState(
    val citiesList: List<CityEntity> = listOf()
)
