package com.example.parasol.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.parasol.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreenTopAppBar(
    navDrawer: () -> Unit,
    citySearch: () -> Unit,
    retryAction: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    textInTopBar: String
) {
    LargeTopAppBar(
        title = {
            Text(text = textInTopBar)
        },
        navigationIcon = {
            IconButton(onClick = navDrawer) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {
            Row {

            IconButton(onClick = citySearch) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.city_search)
                    )
                }
                IconButton(onClick = retryAction) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.retry)
                    )
                }
            }

        },
        scrollBehavior = scrollBehavior
    )
}