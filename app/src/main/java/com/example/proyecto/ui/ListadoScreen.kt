package com.example.proyecto.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyecto.R
import com.example.proyecto.data.model.Transaction
import com.example.proyecto.ui.MainViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Consider renaming if this file is ListadoScreen and not AhorroScreen
enum class ListadoSubScreen {
    Ingresos,
    Gastos,
    AddIngresoForm,
    AddGastoForm
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatCurrency(amount: Double): String {
    val numberFormat = NumberFormat.getCurrencyInstance(Locale("es", "PE"))
    return numberFormat.format(amount)
}

@Composable
fun ListadoScreen(viewModel: MainViewModel) {
    var currentSubScreen by remember { mutableStateOf(ListadoSubScreen.Ingresos) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }
    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }

    val onDeleteRequestHandler = { transaction: Transaction ->
        transactionToDelete = transaction
        showDeleteConfirmationDialog = true
    }

    val onEditRequestHandler = { transaction: Transaction ->
        transactionToEdit = transaction
        currentSubScreen = if (transaction.tipo == "Ingreso") ListadoSubScreen.AddIngresoForm else ListadoSubScreen.AddGastoForm
    }

    val onDismissForm = {
        currentSubScreen = if (transactionToEdit?.tipo == "Ingreso" || currentSubScreen == ListadoSubScreen.AddIngresoForm && transactionToEdit == null) ListadoSubScreen.Ingresos else ListadoSubScreen.Gastos
        transactionToEdit = null // Reset transactionToEdit when form is dismissed
    }


    if (showDeleteConfirmationDialog && transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmationDialog = false
                transactionToDelete = null
            },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar esta transacción: ${transactionToDelete?.descripcion}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        transactionToDelete?.let { viewModel.deleteTransaction(it) }
                        showDeleteConfirmationDialog = false
                        transactionToDelete = null
                    }
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmationDialog = false
                        transactionToDelete = null
                    }
                ) { Text("Cancelar") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (currentSubScreen == ListadoSubScreen.Ingresos || currentSubScreen == ListadoSubScreen.Gastos) {
            ListadoNavigationTabs(
                selectedSubScreen = currentSubScreen,
                onTabSelected = { newScreen -> currentSubScreen = newScreen }
            )
        }

        Box(modifier = Modifier
            .weight(1f)
            .padding(top = 8.dp)) {
            when (currentSubScreen) {
                ListadoSubScreen.Ingresos -> IngresosContentLayout(
                    viewModel = viewModel,
                    onNavigateToAddIngreso = {
                        transactionToEdit = null // Ensure edit state is clear
                        currentSubScreen = ListadoSubScreen.AddIngresoForm
                    },
                    onEditRequest = onEditRequestHandler,
                    onDeleteRequest = onDeleteRequestHandler
                )
                ListadoSubScreen.Gastos -> GastosContentLayout(
                    viewModel = viewModel,
                    onNavigateToAddGasto = {
                        transactionToEdit = null // Ensure edit state is clear
                        currentSubScreen = ListadoSubScreen.AddGastoForm
                    },
                    onEditRequest = onEditRequestHandler,
                    onDeleteRequest = onDeleteRequestHandler
                )
                ListadoSubScreen.AddIngresoForm -> AddIngresoFormLayout(
                    viewModel = viewModel,
                    transactionToEdit = transactionToEdit,
                    onDismiss = onDismissForm
                )
                ListadoSubScreen.AddGastoForm -> AddGastoFormLayout(
                    viewModel = viewModel,
                    transactionToEdit = transactionToEdit,
                    onDismiss = onDismissForm
                )
            }
        }
    }
}

@Composable
private fun ListadoNavigationTabs(
    selectedSubScreen: ListadoSubScreen,
    onTabSelected: (ListadoSubScreen) -> Unit
) {
    val tabs = listOf(ListadoSubScreen.Ingresos, ListadoSubScreen.Gastos)
    TabRow(selectedTabIndex = tabs.indexOf(selectedSubScreen).coerceAtLeast(0)) {
        tabs.forEach { subScreen ->
            val title = if (subScreen == ListadoSubScreen.Ingresos) "INGRESOS" else "GASTOS"
            val iconRes = if (subScreen == ListadoSubScreen.Ingresos) R.drawable.baseline_add_24 else R.drawable.outline_add_shopping_cart_24
            LeadingIconTab(
                selected = selectedSubScreen == subScreen,
                onClick = { onTabSelected(subScreen) },
                text = { Text(title) },
                icon = { Icon(painterResource(id = iconRes), contentDescription = title) }
            )
        }
    }
}

@Composable
fun IngresosContentLayout(
    viewModel: MainViewModel,
    onNavigateToAddIngreso: () -> Unit,
    onEditRequest: (Transaction) -> Unit,
    onDeleteRequest: (Transaction) -> Unit
) {
    val ingresos by viewModel.ingresos.collectAsState(initial = emptyList())
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Button(onClick = onNavigateToAddIngreso, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Text("Agregar Ingreso")
        }
        if (ingresos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay ingresos registrados.") }
        } else {
            LazyColumn(contentPadding = PaddingValues(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ingresos, key = { it.id }) { ingreso ->
                    TransactionCard(transaction = ingreso, onEdit = { onEditRequest(ingreso) }, onDelete = { onDeleteRequest(ingreso) })
                }
            }
        }
    }
}

@Composable
fun GastosContentLayout(
    viewModel: MainViewModel,
    onNavigateToAddGasto: () -> Unit,
    onEditRequest: (Transaction) -> Unit,
    onDeleteRequest: (Transaction) -> Unit
) {
    val gastos by viewModel.gastos.collectAsState(initial = emptyList())
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Button(onClick = onNavigateToAddGasto, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Text("Agregar Gasto")
        }
        if (gastos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay gastos registrados.") }
        } else {
            LazyColumn(contentPadding = PaddingValues(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(gastos, key = { it.id }) { gasto ->
                    TransactionCard(transaction = gasto, onEdit = { onEditRequest(gasto) }, onDelete = { onDeleteRequest(gasto) })
                }
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: Transaction, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = transaction.descripcion, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Monto: ${formatCurrency(transaction.monto)}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Fecha: ${formatDate(transaction.fecha)}", style = MaterialTheme.typography.bodySmall)
            }
            Text(text = "Categoría: ${transaction.categoria}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, contentDescription = "Editar") }
                IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = "Eliminar") }
            }
        }
    }
}

@Composable
fun AddIngresoFormLayout(
    viewModel: MainViewModel,
    transactionToEdit: Transaction?,
    onDismiss: () -> Unit
) {
    var montoStr by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    val categoriasIngreso = listOf("Salario", "Regalo", "Venta", "Otro")
    var selectedCategoria by remember { mutableStateOf(categoriasIngreso[0]) }
    var isMontoError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isEditMode = transactionToEdit != null

    LaunchedEffect(transactionToEdit) {
        if (isEditMode) {
            transactionToEdit?.let {
                montoStr = it.monto.toString()
                descripcion = it.descripcion
                selectedCategoria = it.categoria
            }
        } else {
            // Reset fields for add mode if needed, though they are initialized to empty/default
            montoStr = ""
            descripcion = ""
            selectedCategoria = categoriasIngreso[0]
        }
    }

    fun validateMonto(text: String): Boolean = text.toDoubleOrNull() != null && text.toDouble() > 0

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()).selectableGroup(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isEditMode) "Editar Ingreso" else "Agregar Ingreso",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = montoStr,
            onValueChange = { montoStr = it; isMontoError = !validateMonto(it) },
            label = { Text("Monto (S/.)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = isMontoError,
            modifier = Modifier.fillMaxWidth()
        )
        if (isMontoError) {
            Text("Ingrese un monto válido y mayor a 0", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.Start))
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Text("Categoría:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.align(Alignment.Start)) {
            categoriasIngreso.forEach { categoria ->
                Row(
                    Modifier.fillMaxWidth().selectable(selected = (categoria == selectedCategoria), onClick = { selectedCategoria = categoria }, role = Role.RadioButton).padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = (categoria == selectedCategoria), onClick = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = categoria)
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f, fill = false))
        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                isMontoError = !validateMonto(montoStr)
                if (!isMontoError && descripcion.isNotBlank()) {
                    val monto = montoStr.toDouble()
                    if (isEditMode && transactionToEdit != null) {
                        val updatedTransaction = transactionToEdit.copy(
                            monto = monto,
                            descripcion = descripcion,
                            categoria = selectedCategoria
                            // fecha remains original
                        )
                        viewModel.updateTransaction(updatedTransaction)
                    } else {
                        viewModel.addTransaction("Ingreso", selectedCategoria, Date().time, monto, descripcion)
                    }
                    onDismiss()
                } else if (descripcion.isBlank()) {
                    Toast.makeText(context, "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show()
                }
            }) { Text(if (isEditMode) "Actualizar" else "Guardar") }
            Button(onClick = onDismiss) { Text("Cancelar") }
        }
    }
}

@Composable
fun AddGastoFormLayout(
    viewModel: MainViewModel,
    transactionToEdit: Transaction?,
    onDismiss: () -> Unit
) {
    var montoStr by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    val categoriasGasto = listOf("Alimentos", "Transporte", "Casa", "Ocio", "Salud", "Educación", "Otro")
    var selectedCategoria by remember { mutableStateOf(categoriasGasto[0]) }
    var isMontoError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isEditMode = transactionToEdit != null

    LaunchedEffect(transactionToEdit) {
        if (isEditMode) {
            transactionToEdit?.let {
                montoStr = it.monto.toString()
                descripcion = it.descripcion
                selectedCategoria = it.categoria
            }
        } else {
            montoStr = ""
            descripcion = ""
            selectedCategoria = categoriasGasto[0]
        }
    }

    fun validateMonto(text: String): Boolean = text.toDoubleOrNull() != null && text.toDouble() > 0

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()).selectableGroup(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isEditMode) "Editar Gasto" else "Agregar Gasto",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = montoStr,
            onValueChange = { montoStr = it; isMontoError = !validateMonto(it) },
            label = { Text("Monto (S/.)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = isMontoError,
            modifier = Modifier.fillMaxWidth()
        )
        if (isMontoError) {
            Text("Ingrese un monto válido y mayor a 0", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.Start))
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Text("Categoría:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.align(Alignment.Start)) {
            categoriasGasto.forEach { categoria ->
                Row(
                    Modifier.fillMaxWidth().selectable(selected = (categoria == selectedCategoria), onClick = { selectedCategoria = categoria }, role = Role.RadioButton).padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = (categoria == selectedCategoria), onClick = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = categoria)
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f, fill = false))
        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                isMontoError = !validateMonto(montoStr)
                if (!isMontoError && descripcion.isNotBlank()) {
                    val monto = montoStr.toDouble()
                    if (isEditMode && transactionToEdit != null) {
                        val updatedTransaction = transactionToEdit.copy(
                            monto = monto,
                            descripcion = descripcion,
                            categoria = selectedCategoria
                            // fecha remains original
                        )
                        viewModel.updateTransaction(updatedTransaction)
                    } else {
                        viewModel.addTransaction("Gasto", selectedCategoria, Date().time, monto, descripcion)
                    }
                    onDismiss()
                } else if (descripcion.isBlank()) {
                    Toast.makeText(context, "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show()
                }
            }) { Text(if (isEditMode) "Actualizar" else "Guardar") }
            Button(onClick = onDismiss) { Text("Cancelar") }
        }
    }
}
