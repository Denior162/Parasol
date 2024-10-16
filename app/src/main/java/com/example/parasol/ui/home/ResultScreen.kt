package com.example.parasol.ui.home

import android.util.Log
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parasol.R
import com.example.parasol.R.string.high_risk
import com.example.parasol.R.string.low_risk
import com.example.parasol.R.string.moderate_risk
import com.example.parasol.network.model.Forecast
import com.example.parasol.network.model.UvResponse
import com.example.parasol.ui.components.getCardColors
import com.example.parasol.ui.theme.extendedDark
import com.example.parasol.ui.theme.extendedLight
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@Composable
fun ResultScreen(uvResponse: UvResponse, modifier: Modifier) {
    val forecastGroups = remember { groupForecastByIndexLevel(uvResponse.forecast) }
    LazyColumn(
        state = rememberLazyListState(),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            UVRightNowCard(uvResponse)
        }
        items(forecastGroups) { group ->
            ForecastCard(forecastGroup = group)
        }

    }
}

@Composable
fun UVRightNowCard(uvResponse: UvResponse) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp)
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

@Composable
fun ForecastCard(forecastGroup: ForecastGroup) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val extendedColorScheme = if (isSystemInDarkTheme()) extendedDark else extendedLight
    val cardColor = getCardColors(forecastGroup.items.first().uvi, extendedColorScheme)

    Card(
        onClick = { isExpanded = !isExpanded },
        modifier = Modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        colors = cardColor
    ) {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    TextLevelRisk(group = forecastGroup.level)
                    ForecastTimeRange(
                        forecastGroup.items.first().time,
                        forecastGroup.items.last().time
                    )
                }
                IconButton(onClick = { /*TODO*/ }) {
                }
            }
            if (isExpanded) {
                forecastGroup.items.forEach { forecast ->
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
    return forecast.groupBy {
        Pair(getIndexLevel(it.uvi), parseDate(it.time).toLocalDate())
    }.map { (key, items) ->
        ForecastGroup(key.first, items.toMutableList())
    }
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
        Log.e("ParseDateError", "Error parsing date: $dateString", e)
        ZonedDateTime.now()
    }
}


fun formatTime(zonedDateTime: ZonedDateTime): String {
    return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("HH:mm"))
}

@Preview(showBackground = true)
@Composable
fun MyCardPreview() {
    val sampleGroup = ForecastGroup(
        items = mutableListOf(
            Forecast(uvi = 1.9, time = "2024-10-03T12:00:00Z"),
            Forecast(uvi = 1.1, time = "2024-10-03T13:00:00Z")
        ),
        level = UvRiskLevel.LOW
    )
    ForecastCard(forecastGroup = sampleGroup)
}
