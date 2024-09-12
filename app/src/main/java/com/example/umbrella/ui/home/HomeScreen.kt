package com.example.umbrella.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.umbrella.R
import com.example.umbrella.network.Forecast
import com.example.umbrella.network.UvResponse
import com.example.umbrella.ui.components.HomeScreenTopAppBar
import com.example.umbrella.ui.navigation.NavigationDestination
import com.example.umbrella.ui.theme.extendedDark
import com.example.umbrella.ui.theme.extendedLight
import com.example.umbrella.ui.theme.utils.getCardColors
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.home
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToCityEntry: () -> Unit,
    retryAction: () -> Unit,
    indexUiState: StateFlow<IndexUiState>,
    modifier: Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                ExtendedFloatingActionButton(
                    onClick = navigateToCityEntry,

                    ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    Text(text = stringResource(R.string.add_city))
                }

            }
        }
    ) {
        Scaffold(
            topBar = {
                HomeScreenTopAppBar(
                    modifier = Modifier,
                    navDrawer = { scope.launch { drawerState.apply { if (isClosed) open() else close() } } },
                    scrollBehavior = scrollBehavior,
                    retryAction = retryAction
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                val currentIndexUiState by indexUiState.collectAsState(IndexUiState.Loading)
                Column(
                    modifier = Modifier
                ) {
                    when (currentIndexUiState) {
                        is IndexUiState.Loading -> LoadingScreen()
                        is IndexUiState.Success -> ResultScreen(
                            uvResponse = (currentIndexUiState as IndexUiState.Success).indexes,
                            modifier = modifier.fillMaxWidth()
                        )

                        is IndexUiState.Error -> ErrorScreen(retryAction)

                    }
                }
            }
        }
    }
}

fun groupForecastByIndexLevel(forecast: List<Forecast>): List<ForecastGroup> {
    val groups = mutableListOf<ForecastGroup>()
    var currentGroup: ForecastGroup? = null

    forecast.forEach { forecastItem ->
        val level = getIndexLevel(forecastItem.uvi)
        if (currentGroup == null || currentGroup!!.level != level) {
            currentGroup = ForecastGroup(level, mutableListOf())
            groups.add(currentGroup!!)
        }
        currentGroup!!.items.add(forecastItem)
    }
    return groups
}

fun getIndexLevel(uvi: Double): String {
    return when {
        uvi <= 2 -> "Low risk"
        uvi <= 5 -> "Moderate risk"
        uvi <= 7 -> "High risk"
        uvi <= 10 -> "Very high risk"
        else -> "Extremely risk"
    }
}

data class ForecastGroup(val level: String, val items: MutableList<Forecast>)

fun parseDate(dateString: String): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    return LocalDateTime.parse(dateString, formatter)
}

fun formatTime(localDateTime: LocalDateTime): String {
    return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
}


@Composable
fun ResultScreen(
    uvResponse: UvResponse, modifier: Modifier
) {
    val forecastGroups = remember { groupForecastByIndexLevel(uvResponse.forecast) }

    Column(modifier = modifier) {
        Text(text = "${uvResponse.now.uvi}")
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(forecastGroups) { group ->
                ForecastGroupCard(group = group)
            }
        }
    }
}

@Composable
fun ForecastGroupCard(group: ForecastGroup) {
    var isExpanded by remember { mutableStateOf(false) }
    val extendedColorScheme = if (isSystemInDarkTheme()) extendedDark else extendedLight
    val cardColor = getCardColors(group.items.first().uvi, extendedColorScheme)
    Card(
        onClick = { isExpanded = !isExpanded },
        modifier = Modifier
            .padding(8.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow
                )
            ), colors = cardColor
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row {
                Column(
                    modifier = Modifier

                        .padding(8.dp)
                        .weight(1f)
                ) {
                    Text(text = group.level, style = MaterialTheme.typography.headlineLarge)
                    val startTime = formatTime(parseDate(group.items.first().time))
                    val endTime = formatTime(parseDate(group.items.last().time))
                    Text(
                        text = "$startTime - $endTime",
                        style = MaterialTheme.typography.bodySmall
                    )

                }
                IconButton(
                    onClick = { isExpanded = !isExpanded }, modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = "R.string.expand_button_content_description)"
                    )
                }
            }
        }
        if (isExpanded) {
            group.items.forEach { forecast ->
                Row {
                    val forecastText = formatTime(parseDate(forecast.time))
                    Text(
                        text = "${forecast.uvi}",
                        modifier = Modifier.padding(8.dp)
                    )
                    Text(text = forecastText, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(retryAction: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = retryAction) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Text(text = "Retry")
        }
    }
}

@Composable
fun LoadingScreen() {
    CircularProgressIndicator()
}