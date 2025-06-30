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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

enum class AhorroSubScreen {
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
fun AhorroScreen(viewModel: MainViewModel) {
    var currentSubScreen by remember { mutableStateOf(AhorroSubScreen.Ingresos) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (currentSubScreen == AhorroSubScreen.Ingresos || currentSubScreen == AhorroSubScreen.Gastos) {
            AhorroNavigationTabs(
                selectedSubScreen = currentSubScreen,
                onTabSelected = { newScreen ->
                    currentSubScreen =
                        if (newScreen == AhorroSubScreen.Ingresos) AhorroSubScreen.Ingresos else AhorroSubScreen.Gastos
                }
            )
        }

        Box(modifier = Modifier
            .weight(1f)
            .padding(top = 8.dp)) {
            when (currentSubScreen) {
                AhorroSubScreen.Ingresos -> IngresosContentLayout(
                    viewModel = viewModel,
                    onNavigateToAddIngreso = { currentSubScreen = AhorroSubScreen.AddIngresoForm }
                )

                AhorroSubScreen.Gastos -> GastosContentLayout(
                    viewModel = viewModel,
                    onNavigateToAddGasto = { currentSubScreen = AhorroSubScreen.AddGastoForm }
                )

                AhorroSubScreen.AddIngresoForm -> AddIngresoFormLayout(
                    viewModel = viewModel,
                    onDismiss = { currentSubScreen = AhorroSubScreen.Ingresos }
                )

                AhorroSubScreen.AddGastoForm -> AddGastoFormLayout(
                    viewModel = viewModel,
                    onDismiss = { currentSubScreen = AhorroSubScreen.Gastos }
                )
            }
        }
    }
}

@Composable
private fun AhorroNavigationTabs(
    selectedSubScreen: AhorroSubScreen,
    onTabSelected: (AhorroSubScreen) -> Unit
) {
    val tabs = listOf(AhorroSubScreen.Ingresos, AhorroSubScreen.Gastos)

    TabRow(
        selectedTabIndex = tabs.indexOf(selectedSubScreen).coerceAtLeast(0)
    ) {
        tabs.forEach { subScreen ->
            val title = if (subScreen == AhorroSubScreen.Ingresos) "INGRESOS" else "GASTOS"
            val iconRes =
                if (subScreen == AhorroSubScreen.Ingresos) R.drawable.baseline_add_24 else R.drawable.outline_add_shopping_cart_24
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
fun IngresosContentLayout(viewModel: MainViewModel, onNavigateToAddIngreso: () -> Unit) {
    val ingresos by viewModel.ingresos.collectAsState(initial = emptyList<Transaction>())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Button(
            onClick = onNavigateToAddIngreso,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Agregar Ingreso")
        }

        if (ingresos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay ingresos registrados.")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ingresos, key = { it.id }) { ingreso ->
                    IngresoCard(ingreso = ingreso)
                }
            }
        }
    }
}

@Composable
fun IngresoCard(ingreso: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = ingreso.descripcion,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Monto: ${formatCurrency(ingreso.monto)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Fecha: ${formatDate(ingreso.fecha)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "Categoría: ${ingreso.categoria}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun GastosContentLayout(viewModel: MainViewModel, onNavigateToAddGasto: () -> Unit) {
    val gastos by viewModel.gastos.collectAsState(initial = emptyList<Transaction>())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Button(
            onClick = onNavigateToAddGasto,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Agregar Gasto")
        }

        if (gastos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay gastos registrados.")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(gastos, key = { it.id }) { gasto ->
                    IngresoCard(ingreso = gasto)
                }
            }
        }
    }
}

@Composable
fun AddIngresoFormLayout(viewModel: MainViewModel, onDismiss: () -> Unit) {
    var montoStr by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    val categoriasIngreso = listOf("Salario", "Regalo", "Venta", "Otro")
    var selectedCategoria by remember { mutableStateOf(categoriasIngreso[0]) }
    var isMontoError by remember { mutableStateOf(false) }
    val context = LocalContext.current // Get context for Toast

    fun validateMonto(text: String): Boolean {
        return text.toDoubleOrNull() != null && text.toDouble() > 0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .selectableGroup(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Formulario para Agregar Ingreso",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = montoStr,
            onValueChange = {
                montoStr = it
                isMontoError = !validateMonto(it)
            },
            label = { Text("Monto (S/.)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = isMontoError,
            modifier = Modifier.fillMaxWidth()
        )
        if (isMontoError) {
            Text(
                text = "Ingrese un monto válido y mayor a 0",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            singleLine = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Categoría:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.align(Alignment.Start)) {
            categoriasIngreso.forEach { categoria ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (categoria == selectedCategoria),
                            onClick = { selectedCategoria = categoria },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (categoria == selectedCategoria),
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = categoria)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                isMontoError = !validateMonto(montoStr)
                if (!isMontoError && descripcion.isNotBlank()) {
                    viewModel.addTransaction(
                        tipo = "Ingreso",
                        categoria = selectedCategoria,
                        fecha = Date().time,
                        monto = montoStr.toDouble(),
                        descripcion = descripcion
                    )
                    onDismiss()
                } else if (descripcion.isBlank()) {
                    Toast.makeText(
                        context,
                        "La descripción no puede estar vacía",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
                Text("Guardar")
            }
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    }
}

@Composable
fun AddGastoFormLayout(viewModel: MainViewModel, onDismiss: () -> Unit) {
    var montoStr by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    val categoriasGasto =
        listOf("Alimentos", "Transporte", "Casa", "Ocio", "Salud", "Educación", "Otro")
    var selectedCategoria by remember { mutableStateOf(categoriasGasto[0]) }
    var isMontoError by remember { mutableStateOf(false) }
    val context = LocalContext.current // Get context for Toast

    fun validateMonto(text: String): Boolean {
        return text.toDoubleOrNull() != null && text.toDouble() > 0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .selectableGroup(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Formulario para Agregar Gasto",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = montoStr,
            onValueChange = {
                montoStr = it
                isMontoError = !validateMonto(it)
            },
            label = { Text("Monto (S/.)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = isMontoError,
            modifier = Modifier.fillMaxWidth()
        )
        if (isMontoError) {
            Text(
                text = "Ingrese un monto válido y mayor a 0",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            singleLine = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Categoría:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.align(Alignment.Start)) {
            categoriasGasto.forEach { categoria ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (categoria == selectedCategoria),
                            onClick = { selectedCategoria = categoria },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (categoria == selectedCategoria),
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = categoria)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                isMontoError = !validateMonto(montoStr)
                if (!isMontoError && descripcion.isNotBlank()) {
                    viewModel.addTransaction(
                        tipo = "Gasto",
                        categoria = selectedCategoria,
                        fecha = Date().time,
                        monto = montoStr.toDouble(),
                        descripcion = descripcion
                    )
                    onDismiss()
                } else if (descripcion.isBlank()) {
                    Toast.makeText(
                        context,
                        "La descripción no puede estar vacía",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
                Text("Guardar")
            }
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    }
}