package com.example.umbrella.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.umbrella.data.CityEntity

@Composable
fun CitiesForDrawer(
    cityList: List<CityEntity>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        if (cityList.isEmpty()) {
            Text(
                text = "добавьте первый город",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        } else {
            ListOFCitiesInDrawer(
                cityList = cityList,
                onItemClick = { onItemClick(it.id) },
                contentPadding = contentPadding
            )
        }
    }
}

@Composable
private fun ListOFCitiesInDrawer(
    cityList: List<CityEntity>,
    onItemClick: (CityEntity) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {

        items(items = cityList, key = { it.id }) { city ->
            CityElementInDrawer(
                city = city, selectedCity = {})
        }
    }
}

@Composable
private fun CityElementInDrawer(
    city: CityEntity, modifier: Modifier = Modifier,
    selectedCity: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(text = city.name) },
        selected = false,
        onClick = selectedCity
    )
}