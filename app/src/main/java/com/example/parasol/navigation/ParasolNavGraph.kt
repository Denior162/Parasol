package com.example.parasol.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.parasol.ui.city.search.CitySearchScreen
import com.example.parasol.ui.city.search.CitySearchViewModel
import com.example.parasol.ui.home.HomeScreen
import com.example.parasol.ui.home.HomeViewModel

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object CitySearch : Screen("city_search")
}


@Composable
fun ParasolNavHost(
    navController: NavHostController,
    citiesDrawerAction: () -> Unit
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val searchViewModel: CitySearchViewModel = hiltViewModel()
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                indexUiState = homeViewModel.indexUiState,
                navigateToCitySearch = {
                    try {
                        navController.navigate(Screen.CitySearch.route)
                    } catch (e: Exception) {
                        Log.e(
                            "NavigationError",
                            "Error navigating to CitySearchDestination", e
                        )
                    }
                },
                retryAction = { homeViewModel.retryAction() },
                citiesDrawerAction = citiesDrawerAction
            )
        }
        composable(route = Screen.CitySearch.route) {
            CitySearchScreen(
                viewModel = searchViewModel,
                navigateBack = { navController.popBackStack() },
                searchUiState = searchViewModel.cities,
            )
        }
    }
}