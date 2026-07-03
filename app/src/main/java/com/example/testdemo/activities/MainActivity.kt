package com.example.testdemo.activities

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.testdemo.R

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
