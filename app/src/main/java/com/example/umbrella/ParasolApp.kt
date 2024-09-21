package com.example.umbrella

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.umbrella.ui.navigation.ParasolNavHost

@Composable
fun ParasolApp(navController: NavHostController = rememberNavController()) {
    ParasolNavHost(navController = navController)
}