package com.example.parasol

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.parasol.navigation.ParasolNavHost

@Composable
fun ParasolApp(navController: NavHostController = rememberNavController()) {
    ParasolNavHost(navController = navController)
}