package com.example.umbrella.ui.home.uiStateScreens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.umbrella.model.Forecast
import com.example.umbrella.model.UvResponse
import com.example.umbrella.ui.theme.extendedDark
import com.example.umbrella.ui.theme.extendedLight
import com.example.umbrella.ui.theme.utils.getCardColors
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ResultScreen(
    uvResponse: UvResponse, modifier: Modifier
) {
    val forecastGroups = remember { groupForecastByIndexLevel(uvResponse.forecast) }
    Column(modifier = modifier.padding(8.dp)) {
        Card {
            Text(
                text = "${uvResponse.now.uvi}",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = "UV Index right now",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(8.dp)
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
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
            .padding(4.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow
                )
            ),
        colors = cardColor
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row {
                Column(
                    modifier = Modifier

                        .padding(8.dp)
                        .weight(1f)
                ) {
                    Text(text = group.level, style = MaterialTheme.typography.bodyLarge)
                    val startTime = formatTime(parseDate(group.items.first().time))
                    val endTime = formatTime(parseDate(group.items.last().time))
                    Text(
                        text = "$startTime - $endTime", style = MaterialTheme.typography.bodyMedium
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
                        text = "${forecast.uvi}", modifier = Modifier.padding(8.dp)
                    )
                    Text(text = forecastText, modifier = Modifier.padding(8.dp))
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