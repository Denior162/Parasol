package com.example.parasol.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.example.parasol.ui.components.HomeScreenTopAppBar
import kotlinx.coroutines.flow.StateFlow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToCitySearch: () -> Unit,
    indexUiState: StateFlow<IndexUiState>,
    retryAction: () -> Unit,
    citiesDrawerAction: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val currentIndexUiState by indexUiState.collectAsState(IndexUiState.Loading)

    Scaffold(topBar = {
        HomeScreenTopAppBar(
            citiesDrawerAction = citiesDrawerAction,
            scrollBehavior = scrollBehavior,
            navigateToCitySearch = navigateToCitySearch,
            retryAction = retryAction
        )
    }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            when (currentIndexUiState) {
                is IndexUiState.Loading -> LoadingScreen()
                is IndexUiState.Success -> ResultScreen(
                    uvResponse = (currentIndexUiState as IndexUiState.Success).indexes,
                    modifier = Modifier.nestedScroll(connection = scrollBehavior.nestedScrollConnection)
                )

                is IndexUiState.Error -> ErrorScreen(
                    retryAction = retryAction,
                    errorMessage = (currentIndexUiState as IndexUiState.Error).errorMessage
                )
            }
        }
    }
}
