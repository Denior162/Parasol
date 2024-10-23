package com.example.parasol.ui.citySearch

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.parasol.R
import com.example.parasol.data.CityEntity
import com.example.parasol.network.model.City
import com.example.parasol.ui.home.ErrorScreen
import com.example.parasol.ui.home.LoadingScreen
import kotlinx.coroutines.flow.StateFlow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySearchScreen(
    navigateBack: () -> Unit,
    viewModel: CitySearchViewModel = hiltViewModel(),
    searchUiState: StateFlow<SearchUiState>,
    reverseSearchUiState: StateFlow<ReverseSearchUiState>
) {
    var query by rememberSaveable { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val city by viewModel.homeUiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.getCurrentLocation(context)
            viewModel.location?.let { _ ->
                viewModel.searchCityByCurrentLocation()
                showDialog = true
            } ?: run {
                viewModel.setError(CitySearchViewModel.ErrorType.LocationUnavailable)
            }
        } else {
            viewModel.setError(CitySearchViewModel.ErrorType.PermissionDenied)
        }
    }

    Scaffold(topBar = {
        SearchBar(modifier = Modifier.fillMaxWidth(), inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = {
                    query = it
                    viewModel.searchCityByName(query)
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
            Column(modifier = Modifier.padding(16.dp)) {
                when (currentCitySearchUiState) {
                    is SearchUiState.Loading -> LoadingScreen()
                    is SearchUiState.Success -> {
                        val cities = (currentCitySearchUiState as SearchUiState.Success).result
                        CitySearchResultsList(
                            cities,
                            onSaveCity = { city ->
                                viewModel.addCityToRepository(city)
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
    },
        bottomBar = {
            CitiesBottomAppBar(locationAction = {
                viewModel.searchCityByCurrentLocation()
                locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                viewModel.searchCityByCurrentLocation()
                showDialog = true
            }
            )
        }
    )
    { innerPadding ->
        val reverseSearchedCityUiState by reverseSearchUiState.collectAsState(ReverseSearchUiState.Loading)

        if (showDialog) {
            LocationCityAlertDialog(
                onDismissRequest = { showDialog = false },
                addCityToDatabase = {
                    if (reverseSearchedCityUiState is ReverseSearchUiState.Success) {
                        viewModel.addCityToRepository((reverseSearchedCityUiState as ReverseSearchUiState.Success).result)
                        showDialog = false
                    }
                },
                addCityButtonEnabling = when (reverseSearchedCityUiState) {
                    is ReverseSearchUiState.Loading -> false
                    is ReverseSearchUiState.Success -> true
                    is ReverseSearchUiState.Error -> false // Optionally handle error state
                },
                alertText = {
                    Text(
                        text = when (reverseSearchedCityUiState) {
                            ReverseSearchUiState.Error -> "Error with city search"
                            ReverseSearchUiState.Loading -> "Loading..."
                            is ReverseSearchUiState.Success -> (reverseSearchedCityUiState as ReverseSearchUiState.Success).result.displayName
                        }
                    )
                }
            )
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            AddedCitiesList(
                cityList = city.citiesList,
                deleteCity = { city ->
                    viewModel.deleteCity(city)
                }
            )
        }
    }
}

@Composable
fun CitySearchResultsList(
    cities: List<City>,
    onSaveCity: (City) -> Unit,
    navigateBack: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cities) { city ->
            CityItem(
                cityName = city.displayName,
                action = {
                    onSaveCity(
                        city
                    )
                    navigateBack()
                },
                imageVector = Icons.Default.Add,
                contentDescription = "Add ${city.name} to list"
            )
        }
    }
}

@Composable
fun AddedCitiesList(
    cityList: List<CityEntity>,
    deleteCity: (CityEntity) -> Unit
) {

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.already_added_cities),
                modifier = Modifier.padding(4.dp)
            )
        }

        items(items = cityList) { city ->
            CityItem(
                cityName = city.name,
                action = { deleteCity(city) },
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete ${city.name} from list"
            )
        }
    }
}

@Composable
fun CityItem(
    cityName: String,
    action: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = cityName,
                modifier = Modifier.weight(1F)
            )
            IconButton(onClick = action) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = contentDescription,
                )
            }
        }
    }
}


@Composable
fun CitiesBottomAppBar(locationAction: () -> Unit) {
    BottomAppBar(actions = {
        IconButton(onClick = { /*TODO*/ }) {

        }
    }, floatingActionButton = {
        FloatingActionButton(onClick = locationAction) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = stringResource(R.string.get_city_with_location)
            )
        }
    })
}

@Composable
fun LocationCityAlertDialog(
    addCityToDatabase: () -> Unit,
    addCityButtonEnabling: Boolean,
    onDismissRequest: () -> Unit,
    alertText: @Composable (() -> Unit)?
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(enabled = addCityButtonEnabling, onClick = addCityToDatabase) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Text(text = "Add city")
            }
        },
        text = alertText
    )
}