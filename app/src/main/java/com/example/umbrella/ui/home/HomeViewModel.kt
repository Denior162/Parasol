package com.example.umbrella.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.umbrella.model.UvResponse
import com.example.umbrella.network.IndexApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface IndexUiState {
    data object Loading : IndexUiState
    data class Success(val indexes: UvResponse) : IndexUiState
    data object Error : IndexUiState
}


class HomeViewModel : ViewModel() {
    private val _indexUiState = MutableStateFlow<IndexUiState>(IndexUiState.Loading)
    val indexUiState: StateFlow<IndexUiState> = _indexUiState

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