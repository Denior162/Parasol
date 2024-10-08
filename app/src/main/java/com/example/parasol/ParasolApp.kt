package com.example.parasol

import android.util.Log
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.parasol.navigation.ParasolNavHost
import com.example.parasol.ui.components.CitiesModalDrawer
import com.example.parasol.ui.home.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun ParasolApp(
    navController: NavHostController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectedCityId by viewModel.selectedCityId.collectAsState()

    fun drawerOpenOrClose() {
        scope.launch {
            drawerState.apply {
                if (isClosed) open()
                else close()
            }
        }
    }

    CitiesModalDrawer(
        cityList = homeUiState.citiesList,
        onCitySelected = { selectedCity ->
            Log.d("CitiesModalDrawer", "City selected: ${selectedCity.name}")
            viewModel.setSelectedCity(selectedCity)
            drawerOpenOrClose()
        },
        selectedCityId = selectedCityId,
        drawerState = drawerState, drawerAction = {
            drawerOpenOrClose()
        }
    )
    {
        ParasolNavHost(navController = navController, drawerOpening = drawerOpenOrClose())
    }
}