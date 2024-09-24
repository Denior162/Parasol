package com.example.parasol.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.parasol.ui.AppViewModelProvider
import com.example.parasol.ui.city.search.CitySearchDestination
import com.example.parasol.ui.city.search.CitySearchScreen
import com.example.parasol.ui.city.search.CitySearchViewModel
import com.example.parasol.ui.home.HomeDestination
import com.example.parasol.ui.home.HomeScreen
import com.example.parasol.ui.home.HomeViewModel

@Composable
fun ParasolNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                indexUiState = homeViewModel.indexUiState,
                navigateToCitySearch = {
                    try {
                        navController.navigate(CitySearchDestination.route)
                    } catch (e: Exception) {
                        Log.e(
                            "NavigationError",
                            "Error navigating to CitySearchDestination", e
                        )
                    }
                },
                retryAction = homeViewModel::getUVIs,
            )
        }
        composable(route = CitySearchDestination.route) {
            val searchViewModel: CitySearchViewModel =
                viewModel(factory = AppViewModelProvider.Factory)
            CitySearchScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateBack = { navController.popBackStack() },
                searchUiState = searchViewModel.cities
            )
        }
    }
}