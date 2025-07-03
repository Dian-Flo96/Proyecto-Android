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
import com.example.proyecto.ui.MainViewModel
import com.example.proyecto.ui.PromocionesViewModel
import com.example.proyecto.ui.PromocionesViewModelFactory
import com.example.proyecto.ui.MockSearchServiceRepository // Import mock
import com.example.proyecto.ui.MockGeminiRecommendationServiceRepository // Import mock

class MainActivity : ComponentActivity() {

    private val transactionRepository by lazy { (application as Wally).transactionRepository }

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(transactionRepository)
    }

    private val promocionesViewModel: PromocionesViewModel by viewModels {
        // Instantiate and pass the mock repositories
        PromocionesViewModelFactory(
            searchService = MockSearchServiceRepository(),
            geminiService = MockGeminiRecommendationServiceRepository()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Pass both ViewModels to MainScreen
                    MainScreen(
                        mainViewModel = mainViewModel,
                        promocionesViewModel = promocionesViewModel
                    )
                }
            }
        }
    }
}
