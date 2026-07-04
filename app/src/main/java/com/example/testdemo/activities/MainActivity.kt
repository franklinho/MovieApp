package com.example.testdemo.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.testdemo.MovieApplication
import com.example.testdemo.ui.MovieAppNavHost
import com.example.testdemo.ui.theme.TestDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as MovieApplication).container
        setContent {
            TestDemoTheme {
                MovieAppNavHost(movieRepository = appContainer.movieRepository)
            }
        }
    }
}
