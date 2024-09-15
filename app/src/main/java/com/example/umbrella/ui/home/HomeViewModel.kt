package com.example.umbrella.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umbrella.data.CitiesRepository
import com.example.umbrella.data.CityEntity
import com.example.umbrella.model.UvResponse
import com.example.umbrella.network.IndexApi
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


class HomeViewModel(private val citiesRepository: CitiesRepository) : ViewModel() {
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


    init {
        getUVIs()
    }

    fun getUVIs() {
        viewModelScope.launch {
            _indexUiState.value = IndexUiState.Loading
            _indexUiState.value = try {
                val response = IndexApi.retrofitService.getIndexes(
                    latitude = 50.0, longitude = 35.0
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

data class HomeUiState(val citiesList: List<CityEntity> = listOf()
)
