package com.example.notesieve.ui.homescreen.host

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.notesieve.ui.NoteSieveApp
import com.example.notesieve.ui.theme.NoteSieveTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteSieveTheme {
                NoteSieveApp()
            }
        }
    }

}

