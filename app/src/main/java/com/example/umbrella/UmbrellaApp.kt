package com.example.umbrella

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.umbrella.ui.navigation.UmbrellaNavHost

@Composable
fun UmbrellaApp(navController: NavHostController = rememberNavController()) {
    UmbrellaNavHost(navController = navController)
}