package com.example.umbrella.ui.city

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.umbrella.R
import com.example.umbrella.ui.AppViewModelProvider
import com.example.umbrella.ui.components.SecondaryTopAppBarWithBackAction
import com.example.umbrella.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object CityEntryDestination : NavigationDestination {
    override val route = "city_entry"
    override val titleRes = R.string.add_city
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: CityEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)

) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = {
        SecondaryTopAppBarWithBackAction(
            title = stringResource(id = CityEntryDestination.titleRes),
            onNavigateUp = onNavigateUp
        )
    }) { innerPadding ->
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
            AddCityLayout(
                cityUiState = viewModel.cityUiState,
                onCityValueChange = viewModel::updateUiState,
                onSaveClick = {
                    coroutineScope.launch {
                        viewModel.saveCity()
                        navigateBack()
                    }
                },
                modifier = Modifier.padding(innerPadding)
            )
        }

    }
}

@Composable
fun AddCityLayout(
    cityUiState: CityUiState,
    onCityValueChange: (CityDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        CityInputForm(cityDetails = cityUiState.cityDetails, onCityValueChange = onCityValueChange)
        Button(
            onClick = onSaveClick,
            enabled = cityUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save))
        }
    }
}

@Composable
fun CityInputForm(
    cityDetails: CityDetails,
    modifier: Modifier = Modifier,
    onCityValueChange: (CityDetails) -> Unit = {},
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = cityDetails.name,
            onValueChange = { onCityValueChange(cityDetails.copy(name = it)) },
            label = {
                Text(
                    text = stringResource(R.string.city_name)
                )
            },
            modifier = Modifier.padding(16.dp),
            singleLine = true

        )
        TextField(
            value = cityDetails.latitude,
            onValueChange = { onCityValueChange(cityDetails.copy(latitude = it)) },
            label = { Text(text = stringResource(R.string.latitude)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),

            modifier = Modifier.padding(8.dp),
            singleLine = true

        )
        TextField(
            value = cityDetails.longitude,
            onValueChange = { onCityValueChange(cityDetails.copy(longitude = it)) },
            label = { Text(text = stringResource(R.string.longitude)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.padding(8.dp),
            singleLine = true

        )

    }
}