package com.example.parasol

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NavigationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavigationToCitySearch() {
        hiltRule.inject() // Inject dependencies

        composeTestRule.setContent {
            val navController = rememberNavController()
            ParasolNavHost(navController = navController, citiesDrawerAction = {})
        }

        composeTestRule.onNodeWithText("Navigate to City Search").performClick()
        composeTestRule.onNodeWithText("City Search").assertIsDisplayed()
    }

    @Test
    fun testBackNavigation() {
        hiltRule.inject() // Inject dependencies

        composeTestRule.setContent {
            val navController = rememberNavController()
            ParasolNavHost(navController = navController, citiesDrawerAction = {})
        }

        composeTestRule.onNodeWithText("Navigate to City Search").performClick()
        composeTestRule.onNodeWithText("Back").performClick()
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }
}

