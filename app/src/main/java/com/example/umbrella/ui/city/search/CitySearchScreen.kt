package com.example.umbrella.ui.city.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.umbrella.R
import com.example.umbrella.network.geoCoding.City
import com.example.umbrella.ui.AppViewModelProvider
import com.example.umbrella.ui.home.uiStateScreens.LoadingScreen
import com.example.umbrella.ui.navigation.NavigationDestination
import kotlinx.coroutines.flow.StateFlow

object CitySearchDestination : NavigationDestination {
    override val route = "city_search"
    override val titleRes = R.string.city_search
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySearchScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: CitySearchViewModel = viewModel(factory = AppViewModelProvider.Factory),
    searchUiState: StateFlow<SearchUiState>
) {
    var query by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        SearchBar(modifier = Modifier.fillMaxWidth(), inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = {
                    query = it
                    viewModel.getSearchResult(query) // Обновляем результаты поиска
                },
                onSearch = {},
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it },
                leadingIcon = {
                    IconButton(onClick = { onNavigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                id = R.string.back_button
                            )
                        )
                    }
                }
            )
        }, expanded = isExpanded, onExpandedChange = { isExpanded = it }) {
            val currentCitySearchUiState by searchUiState.collectAsState(SearchUiState.Loading)
            Column {
                when (currentCitySearchUiState) {
                    is SearchUiState.Loading -> LoadingScreen()
                    is SearchUiState.Success -> {
                        val cities = (currentCitySearchUiState as SearchUiState.Success).result
                        SearchOutputCityList(
                            cities,
                            onCitySelected = { /* Обработка выбора города */ },
                            onSaveCity = { city ->
                                viewModel.saveCity(city) // Сохраняем город при нажатии на иконку
                            },
                            navigateBack = navigateBack
                        )
                    }

                    is SearchUiState.Error -> Column {

                    }
                }
            }

        }
    }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

        }
    }
}

@Composable
fun SearchOutputCityList(
    cities: List<City>,
    onCitySelected: (City) -> Unit,
    onSaveCity: (City) -> Unit,
    navigateBack: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(cities) { city ->
            Card(modifier = Modifier
                .padding(8.dp)
                .clickable { onCitySelected(city) }) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = city.display_name,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        onSaveCity(city)
                        navigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.add_city)
                        )
                    }
                }
            }
        }
    }
}


