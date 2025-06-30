package com.example.proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.proyecto.ui.MainScreen
import com.example.proyecto.ui.MainViewModel // Explicitly import the MainViewModel from the ui package

class MainActivity : ComponentActivity() {

    private val transactionRepository by lazy { (application as Wally).transactionRepository }

    // Ensure the type is explicitly com.example.proyecto.ui.MainViewModel
    private val mainViewModel: com.example.proyecto.ui.MainViewModel by viewModels {
        MainViewModelFactory(transactionRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel = mainViewModel)
                }
            }
        }
    }
}
