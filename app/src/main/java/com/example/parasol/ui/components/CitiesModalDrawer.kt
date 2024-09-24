package com.example.parasol.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.parasol.R
import com.example.parasol.data.CityEntity

@Composable
fun CitiesModalDrawer(
    cityList: List<CityEntity>,
    drawerState: DrawerState,
    onCitySelected: (CityEntity) -> Unit,
    selectedCityId: Int?,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(drawerContent = {
        ModalDrawerSheet {
            Column {
                Row {
                    Text(text = stringResource(id = R.string.app_name))
                    IconButton(onClick = { /*TODO*/ }) {

                    }
                }
                CitiesForDrawer(
                    cityList = cityList,
                    onCitySelected = onCitySelected,
                    selectedCityId = selectedCityId
                )
            }
        }
    }, modifier = Modifier.padding(), drawerState = drawerState) {
        content()
    }
}