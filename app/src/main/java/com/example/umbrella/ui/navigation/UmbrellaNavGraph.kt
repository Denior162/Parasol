package com.example.umbrella.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.umbrella.ui.city.CityEntryDestination
import com.example.umbrella.ui.city.CityEntryScreen
import com.example.umbrella.ui.home.HomeDestination
import com.example.umbrella.ui.home.HomeScreen
import com.example.umbrella.ui.home.HomeViewModel

@Composable
fun UmbrellaNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            val indexViewModel: HomeViewModel = viewModel()

            HomeScreen(
                indexUiState = indexViewModel.indexUiState,
                navigateToCityEntry = {
                    try {
                        navController.navigate(CityEntryDestination.route)
                    } catch (e: Exception) {
                        Log.e("NavigationError", "Error navigating to CityEntryDestination", e)
                    }
                },
                modifier = Modifier,
                retryAction = indexViewModel::getUVIs,
            )
        }
        composable(route = CityEntryDestination.route) {
            CityEntryScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}