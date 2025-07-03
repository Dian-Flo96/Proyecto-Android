package com.example.proyecto.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.proyecto.data.model.PromotionItem
// You might need to adjust imports if PromocionesViewModel or MainViewModel are not in the same 'ui' package
// import com.example.proyecto.MainViewModel // If add to expense uses MainViewModel

@Composable
fun PromocionesScreen(
    promocionesViewModel: PromocionesViewModel, // Instance provided by DI or ViewModelFactory
    mainViewModel: MainViewModel // For adding to expenses
) {
    val uiState by promocionesViewModel.uiState.collectAsState()
    val searchQuery by promocionesViewModel.searchQuery.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    // Function to open URL
    val openUrl = { url: String? ->
        url?.let {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                context.startActivity(intent)
            } catch (e: Exception) {
                // Handle error, e.g., show a toast
                android.widget.Toast.makeText(context, "Could not open URL: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to handle adding item to expenses
    // This is a placeholder. You'll need to decide how to get all necessary details
    // (category, date, potentially a more precise amount if 'price' is a string)
    // and potentially navigate to a pre-filled AddGastoForm.
    val onAddToExpenses = { item: PromotionItem ->
        val amount = item.price?.replace(Regex("[^0-9.]"), "")?.toDoubleOrNull()
        if (amount != null) {
            // For simplicity, adding directly. In a real app, you might want
            // to open a confirmation dialog or the AddGastoForm pre-filled.
            mainViewModel.addTransaction(
                tipo = "Gasto",
                categoria = "Promoción", // Or a category derived from item
                fecha = System.currentTimeMillis(),
                monto = amount,
                descripcion = item.name
            )
            android.widget.Toast.makeText(context, "${item.name} agregado a gastos.", android.widget.Toast.LENGTH_SHORT).show()
        } else {
            android.widget.Toast.makeText(context, "No se pudo determinar el monto para ${item.name}.", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { promocionesViewModel.onSearchQueryChanged(it) },
            label = { Text("Buscar promociones...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                promocionesViewModel.searchPromotions()
                focusManager.clearFocus()
            })
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Content Area
        when (val state = uiState) {
            is PromocionesUiState.Idle -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ingresa tu búsqueda para ver promociones.")
                }
            }
            is PromocionesUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is PromocionesUiState.Success -> {
                if (state.promotions.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No se encontraron promociones para tu búsqueda.")
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.promotions, key = { it.id }) { item ->
                            PromotionCard(
                                item = item,
                                onOpenUrl = { openUrl(item.webUrl) },
                                onAddToExpenses = { onAddToExpenses(item) }
                            )
                        }
                    }
                }
            }
            is PromocionesUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun PromotionCard(
    item: PromotionItem,
    onOpenUrl: () -> Unit,
    onAddToExpenses: () -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(item.name, style = MaterialTheme.typography.titleMedium)
            item.description?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
            }
            item.price?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Precio: $it", style = MaterialTheme.typography.bodySmall)
            }
            item.source?.let {
                Text("Fuente: $it", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (item.webUrl != null) {
                    IconButton(onClick = onOpenUrl) {
                        Icon(Icons.Filled.Info, contentDescription = "Abrir en Navegador")
                    }
                }
                IconButton(onClick = onAddToExpenses) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = "Agregar a Gastos")
                }
            }
        }
    }
}
