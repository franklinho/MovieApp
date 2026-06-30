package com.example.testdemo.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.testdemo.models.Movie
import com.example.testdemo.ui.theme.TestDemoTheme
import com.example.testdemo.viewmodels.MovieDetailUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MovieDetailScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rendersMovieDetailsAndHandlesBackClick() {
        var backClicked = false

        composeRule.setContent {
            TestDemoTheme {
                MovieDetailScreen(
                    uiState = MovieDetailUiState(
                        movie = Movie(
                            id = 1,
                            title = "Inception",
                            overview = "A thief steals secrets through dream-sharing technology.",
                            backdropPath = null,
                        ),
                        isLoading = false,
                    ),
                    onBack = { backClicked = true },
                    onRetry = {},
                )
            }
        }

        composeRule.onNodeWithText("Inception").assertIsDisplayed()
        composeRule.onNodeWithText("A thief steals secrets through dream-sharing technology.").assertIsDisplayed()
        composeRule.onNodeWithText("Back").performClick()

        assertTrue(backClicked)
    }

    @Test
    fun rendersErrorAndHandlesRetryClick() {
        var retryClicked = false

        composeRule.setContent {
            TestDemoTheme {
                MovieDetailScreen(
                    uiState = MovieDetailUiState(
                        isLoading = false,
                        errorMessage = "Network failed",
                    ),
                    onBack = {},
                    onRetry = { retryClicked = true },
                )
            }
        }

        composeRule.onNodeWithText("Network failed").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").performClick()

        assertTrue(retryClicked)
    }
}
