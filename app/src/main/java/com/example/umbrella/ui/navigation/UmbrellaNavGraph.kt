package com.example.umbrella.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.umbrella.ui.AppViewModelProvider
import com.example.umbrella.ui.city.entry.CityEntryDestination
import com.example.umbrella.ui.city.entry.CityEntryScreen
import com.example.umbrella.ui.city.search.CitySearchDestination
import com.example.umbrella.ui.city.search.CitySearchScreen
import com.example.umbrella.ui.city.search.CitySearchViewModel
import com.example.umbrella.ui.home.HomeDestination
import com.example.umbrella.ui.home.HomeScreen
import com.example.umbrella.ui.home.HomeViewModel

@Composable
fun UmbrellaNavHost(
    navController: NavHostController, modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            val indexViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
            HomeScreen(
                indexUiState = indexViewModel.indexUiState,
                modifier = Modifier,
                navigateToCitySearch = {
                    try {
                        navController.navigate(CitySearchDestination.route)
                    } catch (e: Exception) {
                        Log.e("NavigationError", "Error navigating to CitySearchDestination", e)

                    }
                },
                retryAction = indexViewModel::getUVIs,
            )
        }
        composable(route = CityEntryDestination.route) {
            CityEntryScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateBack = { navController.popBackStack() }
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