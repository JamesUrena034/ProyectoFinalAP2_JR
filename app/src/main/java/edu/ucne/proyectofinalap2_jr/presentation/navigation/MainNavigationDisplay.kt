package edu.ucne.proyectofinalap2_jr.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import edu.ucne.proyectofinalap2_jr.presentation.auth.AuthViewModel
import edu.ucne.proyectofinalap2_jr.presentation.auth.LoginScreen
import edu.ucne.proyectofinalap2_jr.presentation.carrito.CarritoScreen
import edu.ucne.proyectofinalap2_jr.presentation.carrito.CarritoViewModel
import edu.ucne.proyectofinalap2_jr.presentation.categoria.CategoriaListScreen
import edu.ucne.proyectofinalap2_jr.presentation.categoria.CreateCategoriaScreen
import edu.ucne.proyectofinalap2_jr.presentation.home.HomeScreen
import edu.ucne.proyectofinalap2_jr.presentation.pedido.DetallePedidoScreen
import edu.ucne.proyectofinalap2_jr.presentation.pedido.MisPedidosScreen
import edu.ucne.proyectofinalap2_jr.presentation.perfil.PerfilScreen
import edu.ucne.proyectofinalap2_jr.presentation.producto.create.CreateProductoScreen
import edu.ucne.proyectofinalap2_jr.presentation.producto.detail.ProductoDetailScreen
import edu.ucne.proyectofinalap2_jr.presentation.producto.list.ProductoListScreen

@Composable
fun MainNavigationDisplay() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val carritoViewModel: CarritoViewModel = hiltViewModel()

    val isAdmin = authState.user != null &&
            authState.user!!.email?.contains("admin") == true

    val screensWithBottomBar = listOf(
        Screen.Home::class,
        Screen.Categorias::class,
        Screen.Carrito::class,
        Screen.MisPedidos::class,
        Screen.Perfil::class,
        Screen.AdminPedidos::class
    )

    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination

    val showBottomBar = screensWithBottomBar.any {
        currentDestination?.route?.contains(it.simpleName ?: "") == true
    }

    val startDestination = if (authState.user != null) Screen.Home else Screen.Login

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentScreen = Screen.Home,
                    onNavClick = { screen ->
                        navController.navigate(screen) {
                            launchSingleTop = true
                        }
                    },
                    isAdmin = isAdmin
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            composable<Screen.Login> {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.Login) { inclusive = true }
                        }
                    }
                )
            }

            composable<Screen.Home> {
                HomeScreen(
                    onProductoClick = { id ->
                        navController.navigate(Screen.ProductoDetail(id))
                    }
                )
            }

            composable<Screen.Categorias> {
                CategoriaListScreen(
                    onCreateClick = {
                        navController.navigate(Screen.CreateCategoria)
                    },
                    onCategoriaClick = {},
                    isAdmin = isAdmin
                )
            }

            composable<Screen.CreateCategoria> {
                CreateCategoriaScreen(
                    onBack = { navController.navigateUp() }
                )
            }

            composable<Screen.ProductoDetail> {
                val args = it.toRoute<Screen.ProductoDetail>()
                ProductoDetailScreen(
                    productoId = args.productoId,
                    onBack = { navController.navigateUp() },
                    onAgregarAlCarrito = { item ->
                        carritoViewModel.agregarItem(item)
                        navController.navigateUp()
                    }
                )
            }

            composable<Screen.ProductoCreate> {
                val args = it.toRoute<Screen.ProductoCreate>()
                CreateProductoScreen(
                    productoId = args.productoId,
                    onBack = { navController.navigateUp() }
                )
            }

            composable<Screen.Carrito> {
                CarritoScreen(
                    viewModel = carritoViewModel,
                    onPedidoExitoso = {
                        navController.navigate(Screen.MisPedidos) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable<Screen.MisPedidos> {
                MisPedidosScreen(
                    onPedidoClick = { id ->
                        navController.navigate(Screen.DetallePedido(id))
                    }
                )
            }

            composable<Screen.DetallePedido> {
                val args = it.toRoute<Screen.DetallePedido>()
                DetallePedidoScreen(
                    pedidoId = args.pedidoId,
                    onBack = { navController.navigateUp() }
                )
            }

            composable<Screen.AdminPedidos> {
                MisPedidosScreen(
                    onPedidoClick = { id ->
                        navController.navigate(Screen.DetallePedido(id))
                    }
                )
            }

            composable<Screen.Perfil> {
                PerfilScreen(
                    onSignOut = {
                        navController.navigate(Screen.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}