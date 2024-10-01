package com.example.parasol.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.parasol.R
import com.example.parasol.navigation.NavigationDestination
import com.example.parasol.ui.AppViewModelProvider
import com.example.parasol.ui.components.CitiesModalDrawer
import com.example.parasol.ui.components.HomeScreenTopAppBar
import com.example.parasol.ui.home.uiStateScreens.ErrorScreen
import com.example.parasol.ui.home.uiStateScreens.LoadingScreen
import com.example.parasol.ui.home.uiStateScreens.ResultScreen
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.home
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToCitySearch: () -> Unit,
    retryAction: () -> Unit,
    indexUiState: StateFlow<IndexUiState>,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val selectedCityId by viewModel.selectedCityId.collectAsState()
    val currentIndexUiState by indexUiState.collectAsState(IndexUiState.Loading)
    fun drawerAction() {
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
            drawerAction()
            retryAction()
        },
        selectedCityId = selectedCityId,
        drawerState = drawerState, drawerAction = { drawerAction() }
    )
    {
        Scaffold(topBar = {
            HomeScreenTopAppBar(
                navDrawer = { drawerAction() },
                scrollBehavior = scrollBehavior,
                citySearch = navigateToCitySearch,
                retryAction = { retryAction() },
                textInTopBar = stringResource(id = R.string.app_name)
            )
        }, contentWindowInsets = WindowInsets(bottom = 0.dp)) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {
                when (currentIndexUiState) {
                    is IndexUiState.Loading -> LoadingScreen()
                    is IndexUiState.Success -> ResultScreen(
                        uvResponse = (currentIndexUiState as IndexUiState.Success).indexes
                    )

                    is IndexUiState.Error -> ErrorScreen(retryAction)
                }
            }
        }
    }
}