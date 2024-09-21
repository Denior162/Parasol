package com.example.umbrella.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.umbrella.R
import com.example.umbrella.ui.AppViewModelProvider
import com.example.umbrella.ui.components.CitiesForDrawer
import com.example.umbrella.ui.components.HomeScreenTopAppBar
import com.example.umbrella.ui.home.uiStateScreens.ErrorScreen
import com.example.umbrella.ui.home.uiStateScreens.LoadingScreen
import com.example.umbrella.ui.home.uiStateScreens.ResultScreen
import com.example.umbrella.ui.navigation.NavigationDestination
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

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet {
            CitiesForDrawer(cityList = homeUiState.citiesList,
                selectedCityId = selectedCityId,
                onCitySelected = { selectedCity ->
                    viewModel.setSelectedCity(selectedCity)
                    retryAction()
                    scope.launch { drawerState.apply { if (isClosed) open() else close() } }
                }
            )
        }
    }) {
        Scaffold(topBar = {
            HomeScreenTopAppBar(
                navDrawer = { scope.launch { drawerState.apply { if (isClosed) open() else close() } } },
                scrollBehavior = scrollBehavior,
                citySearch = navigateToCitySearch,
                retryAction = retryAction,
            )
        }) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
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