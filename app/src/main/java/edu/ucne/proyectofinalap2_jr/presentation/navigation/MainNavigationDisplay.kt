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
import edu.ucne.proyectofinalap2_jr.presentation.auth.AuthIntent
import edu.ucne.proyectofinalap2_jr.presentation.auth.AuthViewModel
import edu.ucne.proyectofinalap2_jr.presentation.auth.LoginScreen
import edu.ucne.proyectofinalap2_jr.presentation.carrito.CarritoScreen
import edu.ucne.proyectofinalap2_jr.presentation.carrito.CarritoViewModel
import edu.ucne.proyectofinalap2_jr.presentation.categoria.CategoriaListScreen
import edu.ucne.proyectofinalap2_jr.presentation.categoria.CreateCategoriaScreen
import edu.ucne.proyectofinalap2_jr.presentation.categoria.EditCategoriaScreen
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

    val isAdmin = authState.isAdmin

    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route ?: ""

    val screensWithBottomBar = listOf(
        "Home", "Categorias", "Carrito", "MisPedidos",
        "Perfil", "AdminPedidos", "AdminProductos"
    )
    val showBottomBar = screensWithBottomBar.any { currentRoute.contains(it) }

    val currentScreen = when {
        currentRoute.contains("Home") -> Screen.Home
        currentRoute.contains("Categorias") -> Screen.Categorias
        currentRoute.contains("Carrito") -> Screen.Carrito
        currentRoute.contains("MisPedidos") -> Screen.MisPedidos
        currentRoute.contains("AdminPedidos") -> Screen.AdminPedidos
        currentRoute.contains("AdminProductos") -> Screen.AdminProductos
        currentRoute.contains("Perfil") -> Screen.Perfil
        else -> Screen.Home
    }

    LaunchedEffect(authState.user) {
        if (authState.user == null) {
            navController.navigate(Screen.Login) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar && authState.user != null) {
                BottomNavBar(
                    currentScreen = currentScreen,
                    onNavClick = { screen ->
                        navController.navigate(screen) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    isAdmin = isAdmin
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (authState.user != null) Screen.Home else Screen.Login,
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
                    onCategoriaClick = { id ->
                        if (isAdmin) {
                            navController.navigate(Screen.EditCategoria(id))
                        }
                    },
                    isAdmin = isAdmin
                )
            }

            composable<Screen.CreateCategoria> {
                CreateCategoriaScreen(
                    onBack = { navController.navigateUp() }
                )
            }

            composable<Screen.EditCategoria> {
                val args = it.toRoute<Screen.EditCategoria>()
                EditCategoriaScreen(
                    categoriaId = args.categoriaId,
                    onBack = { navController.navigateUp() }
                )
            }

            composable<Screen.AdminProductos> {
                ProductoListScreen(
                    onProductoClick = { id ->
                        if (isAdmin) {
                            navController.navigate(Screen.ProductoCreate(id))
                        } else {
                            navController.navigate(Screen.ProductoDetail(id))
                        }
                    },
                    onCreateClick = {
                        navController.navigate(Screen.ProductoCreate())
                    },
                    isAdmin = isAdmin
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
                        authViewModel.processIntent(AuthIntent.SignOut)
                    }
                )
            }
        }
    }
}