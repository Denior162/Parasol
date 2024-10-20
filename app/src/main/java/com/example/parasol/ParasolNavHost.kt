package com.example.parasol

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.parasol.ui.citySearch.CitySearchScreen
import com.example.parasol.ui.citySearch.CitySearchViewModel
import com.example.parasol.ui.home.HomeScreen
import com.example.parasol.ui.home.HomeViewModel

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object CitySearch : Screen("citySearch")
}


@Composable
fun ParasolNavHost(
    navController: NavHostController,
    citiesDrawerAction: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()

            HomeScreen(
                indexUiState = homeViewModel.indexUiState,
                navigateToCitySearch = { handleNavigation(navController, Screen.CitySearch) },
                retryAction = { homeViewModel.retryAction() },
                citiesDrawerAction = citiesDrawerAction
            )
        }
        composable(route = Screen.CitySearch.route) {
            val searchViewModel: CitySearchViewModel = hiltViewModel()

            CitySearchScreen(
                viewModel = searchViewModel,
                navigateBack = { navController.popBackStack() },
                searchUiState = searchViewModel.cities,
            )
        }
    }
}


private fun handleNavigation(navController: NavHostController, screen: Screen) {
    try {
        navController.navigate(screen.route)
    } catch (e: Exception) {
        Log.e("NavigationError", "Error navigating to ${screen.route}", e)
    }
}
