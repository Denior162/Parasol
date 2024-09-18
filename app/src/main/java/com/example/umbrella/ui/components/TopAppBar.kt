package com.example.umbrella.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.umbrella.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreenTopAppBar(
    navDrawer: () -> Unit,
    citySearch: () -> Unit,
    retryAction: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    LargeTopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SecondaryTopAppBarWithBackAction(
    title: String,
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null

) {
    TopAppBar(title = { Text(text = title) }, modifier = modifier, navigationIcon = {
        IconButton(
            onClick = { onNavigateUp() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_button)
            )
        }
    }, scrollBehavior = scrollBehavior)
}