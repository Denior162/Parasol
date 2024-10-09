package com.example.parasol.ui.city.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.parasol.R
import com.example.parasol.data.CityEntity
import com.example.parasol.network.model.City
import com.example.parasol.ui.home.ErrorScreen
import com.example.parasol.ui.home.HomeViewModel
import com.example.parasol.ui.home.LoadingScreen
import kotlinx.coroutines.flow.StateFlow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySearchScreen(
    navigateBack: () -> Unit,
    viewModel: CitySearchViewModel = hiltViewModel(),
    searchUiState: StateFlow<SearchUiState>
) {
    var query by rememberSaveable { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    val homeViewModel: HomeViewModel = hiltViewModel()
    val city by homeViewModel.homeUiState.collectAsState()

    Scaffold(topBar = {
        SearchBar(modifier = Modifier.fillMaxWidth(), inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = {
                    query = it
                    viewModel.getSearchResult(query)
                },
                placeholder = { Text(text = stringResource(id = R.string.city_search)) },
                onSearch = {/*TODO()*/ },
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it },
                leadingIcon = {
                    IconButton(onClick = { /*TODO*/ }) {

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
                            onCitySelected = { /* TODO() */ },
                            onSaveCity = { city ->
                                viewModel.saveCity(city)
                            },

                            navigateBack = navigateBack
                        )
                    }

                    is SearchUiState.Error -> ErrorScreen(
                        retryAction = { /*TODO()*/ },
                        errorMessage = (""/*TODO()*/)
                    )
                }
            }
        }
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (city.citiesList.isEmpty()) {
                Text(stringResource(R.string.no_cities_text))
            } else {
                ListOfCitiesInSearchScreen(cityList = city.citiesList, deleteCity = { city ->
                    viewModel.deleteCity(city)
                })
            }
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
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Card(
                    onClick = { onCitySelected(city) },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
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
}

@Composable
fun ListOfCitiesInSearchScreen(
    cityList: List<CityEntity>,
    deleteCity: (CityEntity) -> Unit
) {
    LazyColumn {
        item {
            Text(
                text = stringResource(R.string.already_added_cities),
                modifier = Modifier.padding(4.dp)
            )
        }

        items(items = cityList) { city ->
            CityElementInSearch(city = city, deleteCity = { deleteCity(city) })
        }
    }
}

@Composable
fun CityElementInSearch(
    city: CityEntity,
    deleteCity: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)

        ) {
            Text(text = city.name)
            IconButton(onClick = deleteCity) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "delete an icon")
            }
        }

    }
}

