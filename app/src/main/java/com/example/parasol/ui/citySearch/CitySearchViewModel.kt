package com.example.parasol.ui.citySearch

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parasol.data.CitiesRepository
import com.example.parasol.data.CityEntity
import com.example.parasol.network.NominatimReverseApiService
import com.example.parasol.network.NominatimSearchApiService
import com.example.parasol.network.model.City
import com.example.parasol.ui.home.HomeUiState
import com.example.parasol.utils.ErrorHandler.handleError
import com.google.android.gms.location.LocationServices
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

sealed class ReverseSearchUiState {
    data object Loading : ReverseSearchUiState()
    data class Success(val result: City) : ReverseSearchUiState()
    data object Error : ReverseSearchUiState()
}


@HiltViewModel
class CitySearchViewModel @Inject constructor(
    private val citiesRepository: CitiesRepository,
    private val geocodingSearchByCityName: NominatimSearchApiService,
    private val geocodingReverseSearchByCoordinates: NominatimReverseApiService
) : ViewModel() {
    private val _citySearchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val citySearchUiState: StateFlow<SearchUiState> = _citySearchUiState

    private val _cityReverseUiState =
        MutableStateFlow<ReverseSearchUiState>(ReverseSearchUiState.Loading)
    val cityReverseUiState: StateFlow<ReverseSearchUiState> = _cityReverseUiState

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
                val result = geocodingSearchByCityName.searchCities(cityName)
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

    fun searchCityByCurrentLocation() {
        location?.let { loc ->
            searchCityByCoordinates(loc.latitude, loc.longitude)
        } ?: setError(ErrorType.GenericError(""))
    }

    private fun searchCityByCoordinates(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _cityReverseUiState.value = ReverseSearchUiState.Loading
            delay(debounceDelay)
            try {
                val result = geocodingReverseSearchByCoordinates.searchCities(latitude, longitude)
                _cityReverseUiState.value = ReverseSearchUiState.Success(result)
            } catch (e: Exception) {
                handleError(e)
                _cityReverseUiState.value = ReverseSearchUiState.Error
            }
        }
    }


    fun addCityToRepository(city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            val cityEntity = CityEntity(
                name = city.address.city,
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

    var location by mutableStateOf<Location?>(null)
    private var errorMessage by mutableStateOf<String?>(null)

    private fun updateLocation(newLocation: Location) {
        location = newLocation
        errorMessage = null
    }

    sealed class ErrorType {
        data object PermissionDenied : ErrorType()
        data object LocationUnavailable : ErrorType()
        data class GenericError(val message: String) : ErrorType()
    }

    fun setError(error: ErrorType) {
        errorMessage = when (error) {
            is ErrorType.PermissionDenied -> "No permission to access location"
            is ErrorType.LocationUnavailable -> "Failed to get location"
            is ErrorType.GenericError -> error.message
        }
    }


    fun getCurrentLocation(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            setError(ErrorType.PermissionDenied)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
            if (loc != null) {
                updateLocation(loc) // This sets 'location' in ViewModel
                searchCityByCurrentLocation() // Call here if you want to search immediately after getting location
            } else {
                setError(ErrorType.LocationUnavailable)
            }
        }.addOnFailureListener {
            setError(ErrorType.LocationUnavailable)
        }
    }

}
