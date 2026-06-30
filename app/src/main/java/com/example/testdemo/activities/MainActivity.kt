package com.example.testdemo.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.testdemo.ui.MovieAppNavHost
import com.example.testdemo.ui.theme.TestDemoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestDemoTheme {
                MovieAppNavHost()
            }
        }
    }
}
