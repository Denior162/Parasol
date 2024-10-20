package com.example.parasol.utils.useCases

import com.example.parasol.network.CurrentUvApiService
import com.example.parasol.network.model.UvResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FetchUVDataUseCase @Inject constructor(
    private val uvIndexApi: CurrentUvApiService
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double
    ): UvResponse {
        return uvIndexApi.getIndexes(
            latitude,
            longitude
        )
    }
}