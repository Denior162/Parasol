package com.example.parasol.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parasol.data.CityEntity

@Composable
fun ListOfCitiesInSearchScreen(
    cityList: List<CityEntity>
) {
    LazyColumn {
        item { Text(text = "Вже додані міста", modifier = Modifier.padding(4.dp)) }

        items(items = cityList) { city ->
            CityElementInSearch(city = city)
        }
    }
}

@Composable
private fun CityElementInSearch(
    city: CityEntity
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
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "delete an icon")
            }
        }

    }
}

@Composable
fun CitiesForDrawer(
    cityList: List<CityEntity>,
    onCitySelected: (CityEntity) -> Unit,
    modifier: Modifier = Modifier,
    selectedCityId: Int?,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        ListOFCitiesInDrawer(
            cityList = cityList,
            onCitySelected = onCitySelected,
            contentPadding = contentPadding,
            selectedCityId = selectedCityId
        )
    }
}

@Composable
private fun ListOFCitiesInDrawer(
    cityList: List<CityEntity>, onCitySelected: (CityEntity) -> Unit,
    contentPadding: PaddingValues,
    selectedCityId: Int?,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {

        items(items = cityList, key = { it.id }) { city ->
            CityElementInDrawer(
                city = city,
                selectedCity = { onCitySelected(city) },
                isSelected = city.id == selectedCityId
            )
        }
    }
}

@Composable
private fun CityElementInDrawer(
    city: CityEntity,
    isSelected: Boolean, selectedCity: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(text = city.name) },
        selected = isSelected,
        onClick = selectedCity
    )
}