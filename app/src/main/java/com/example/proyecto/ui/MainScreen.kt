package com.example.proyecto.ui // <<<< Ensure this is the package for MainScreen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview // For @Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.proyecto.R // For R.drawable resources
import com.example.proyecto.data.dao.TransactionDao
import com.example.proyecto.data.model.Transaction
import com.example.proyecto.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Date

// Sealed class for navigation routes
sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null, val iconResId: Int? = null) {
    object Inicio : Screen("inicio", "INICIO", iconResId = R.drawable.baseline_home_24)
    object Promociones : Screen("promociones", "PROMOCIONES", iconResId = R.drawable.outline_add_shopping_cart_24)
    object Ahorro : Screen("ahorro", "AHORRO", iconResId = R.drawable.outline_attach_money_24)
}

val navItems = listOf(
    Screen.Inicio,
    Screen.Promociones,
    Screen.Ahorro
)

@OptIn(ExperimentalMaterial3Api::class) // Added OptIn for Scaffold if not present
@Composable
fun MainScreen(viewModel: MainViewModel) { // viewModel comes from com.example.proyecto.ui.MainViewModel
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // Ensure viewModel IS being used here by passing to NavigationGraph
            NavigationGraph(navController = navController, viewModel = viewModel)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        navItems.forEach { screen ->
            NavigationBarItem(
                icon = {
                    if (screen.icon != null) {
                        Icon(screen.icon, contentDescription = screen.title)
                    } else if (screen.iconResId != null) {
                        Icon(painterResource(id = screen.iconResId), contentDescription = screen.title)
                    }
                },
                label = { Text(screen.title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, viewModel: MainViewModel) {
    NavHost(navController = navController, startDestination = Screen.Inicio.route) {
        composable(Screen.Inicio.route) {
            InicioScreen(viewModel = viewModel)
        }
        composable(Screen.Promociones.route) {
            PromocionesScreen(viewModel = viewModel)
        }
        composable(Screen.Ahorro.route) {
            AhorroScreen(viewModel = viewModel)
        }
    }
}

@Composable
fun InicioScreen(viewModel: MainViewModel) {
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList<Transaction>())

    LaunchedEffect(transactions) {
        Log.d("InicioScreen", "Transactions: ${transactions.joinToString { it.descripcion }}")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Inicio Screen - Content will be here")
        Text("Transactions Count: ${transactions.size}")
        Button(onClick = {
            viewModel.addTransaction(
                tipo = "Ingreso",
                categoria = "Salario",
                fecha = Date().time, // Current time as Long
                monto = 2000.0,
                descripcion = "Monthly Salary"
            )
        }) {
            Text("Add Sample Ingreso")
        }
        Button(onClick = {
            viewModel.addTransaction(
                tipo = "Gasto",
                categoria = "Alimentos",
                fecha = Date().time, // Current time as Long
                monto = 50.0,
                descripcion = "Groceries"
            )
        }) {
            Text("Add Sample Gasto")
        }
    }
}

@Composable
fun PromocionesScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Promociones Screen - Content will be here")
    }
}

@Composable
fun AhorroScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ahorro Screen - Content will be here")
    }
}

// --- Preview Setup ---

// Fake DAO for preview - place this within MainScreen.kt or its own file if preferred
class FakeTransactionDaoPreview : TransactionDao { // Renamed to avoid clash if another FakeTransactionDao exists
    override suspend fun insert(transaction: Transaction) {}
    override suspend fun update(transaction: Transaction) {}
    override suspend fun delete(transaction: Transaction) {}
    override fun getTransactionById(id: Long): Flow<Transaction?> = flowOf(null)
    override fun getAllTransactions(): Flow<List<Transaction>> = flowOf(
        listOf( // Sample data for preview
            Transaction(id = 1, tipo = "Ingreso", categoria = "Salario Preview", fecha = Date().time, monto = 2500.0, descripcion = "Preview Salary"),
            Transaction(id = 2, tipo = "Gasto", categoria = "Comida Preview", fecha = Date().time, monto = 75.0, descripcion = "Preview Lunch")
        )
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    // Create a fake repository with the fake DAO
    val fakeRepository = TransactionRepository(FakeTransactionDaoPreview())
    // Pass the fake repository to the MainViewModel for the preview
    MainScreen(viewModel = MainViewModel(fakeRepository))
}
