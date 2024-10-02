package com.example.parasol.ui.home.uiStateScreens

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.parasol.R
import com.example.parasol.R.string.high_risk
import com.example.parasol.R.string.low_risk
import com.example.parasol.R.string.moderate_risk
import com.example.parasol.network.stopLightUVI.Forecast
import com.example.parasol.network.stopLightUVI.UvResponse
import com.example.parasol.ui.components.getCardColors
import com.example.parasol.ui.home.UvRiskLevel
import com.example.parasol.ui.theme.extendedDark
import com.example.parasol.ui.theme.extendedLight
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@Composable
fun ResultScreen(uvResponse: UvResponse, modifier: Modifier) {
    val forecastGroups = remember { groupForecastByIndexLevel(uvResponse.forecast) }
    LazyColumn(state = rememberLazyListState(), modifier = modifier) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "${uvResponse.now.uvi}",
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = stringResource(R.string.uv_index_right_now),
                        style = MaterialTheme.typography.displaySmall
                    )
                }

            }
        }
        items(forecastGroups) { group ->
            ForecastGroupCard(group = group)
        }

    }
}

@Composable
fun ForecastGroupCard(group: ForecastGroup) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val extendedColorScheme = if (isSystemInDarkTheme()) extendedDark else extendedLight
    val cardColor = getCardColors(group.items.first().uvi, extendedColorScheme)
    Card(
        onClick = { isExpanded = !isExpanded },
        modifier = Modifier
            .padding(vertical = 4.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow
                )
            ),
        colors = cardColor
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    TextLevelRisk(group = group.level)
                    ForecastTimeRange(group.items.first().time, group.items.last().time)
                }
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand/Collapse"
                    )
                }
            }

        }
        if (isExpanded) {
            Column {
                group.items.forEach { forecast ->
                    Row {
                        Text(text = "${forecast.uvi}", modifier = Modifier.padding(8.dp))
                        Text(
                            text = formatTime(parseDate(forecast.time)),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun TextLevelRisk(group: UvRiskLevel) {
    Text(
        text = when (group) {
            UvRiskLevel.LOW -> stringResource(low_risk)
            UvRiskLevel.MODERATE -> stringResource(moderate_risk)
            UvRiskLevel.HIGH -> stringResource(high_risk)
            UvRiskLevel.VERY_HIGH -> stringResource(R.string.very_high_risk)
            UvRiskLevel.EXTREMELY_HIGH -> stringResource(R.string.extremely_risk)
        }
    )
}

@Composable
fun ForecastTimeRange(start: String, end: String) {
    val startTime = formatTime(parseDate(start))
    val endTime = formatTime(parseDate(end))
    Text(text = "$startTime - $endTime", style = MaterialTheme.typography.bodyMedium)
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


fun getIndexLevel(uvi: Double): UvRiskLevel {
    return when {
        uvi <= 2 -> UvRiskLevel.LOW
        uvi <= 5 -> UvRiskLevel.MODERATE
        uvi <= 7 -> UvRiskLevel.HIGH
        uvi <= 10 -> UvRiskLevel.VERY_HIGH
        else -> UvRiskLevel.EXTREMELY_HIGH
    }
}


data class ForecastGroup(val level: UvRiskLevel, val items: MutableList<Forecast>)

fun parseDate(dateString: String): ZonedDateTime {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        ZonedDateTime.parse(dateString, formatter.withZone(ZoneId.of("UTC")))
    } catch (e: Exception) {
        ZonedDateTime.now()
    }
}


fun formatTime(zonedDateTime: ZonedDateTime): String {
    return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("HH:mm"))
}