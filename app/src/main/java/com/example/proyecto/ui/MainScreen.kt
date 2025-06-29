package com.example.proyecto.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Budget Tracker") })
        }
    ) { paddingValues ->
        // Content of your screen will go here
        Text(text = "Hello Compose!", modifier = androidx.compose.ui.Modifier.padding(paddingValues))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainScreen(viewModel = MainViewModel()) // This might need a mock ViewModel or Hilt for preview
}