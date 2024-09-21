package com.example.umbrella.ui.welcome

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.umbrella.R
import com.example.umbrella.ui.navigation.NavigationDestination

object WelcomeDestination : NavigationDestination {
    override val route = "welcome"
    override val titleRes = R.string.welcome
}

@Composable
fun WelcomeScreen(
    navigateToCitySearch: () -> Unit,
) {
    Scaffold { innerPadding ->
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(innerPadding)
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.displayLarge
            )
            Text(text = stringResource(R.string.welcome_body))
            Spacer(modifier = Modifier.fillMaxSize(0.5F))
            Button(onClick = navigateToCitySearch, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.add_first_city),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}