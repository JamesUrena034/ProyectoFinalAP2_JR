package edu.ucne.proyectofinalap2_jr.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import edu.ucne.proyectofinalap2_jr.presentation.producto.list.ProductosPorCategoriaScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationDisplay() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val carritoViewModel: CarritoViewModel = hiltViewModel()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isAdmin = authState.isAdmin

    LaunchedEffect(authState.user) {
        if (authState.user == null) {
            navController.navigate(Screen.Login) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    if (authState.user == null) {
        NavHost(
            navController = navController,
            startDestination = Screen.Login
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
        }
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                Spacer(Modifier.height(16.dp))
                Text(
                    "AlquilaFest",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    authState.usuario?.nombre ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    if (isAdmin) "ADMIN" else "USUARIO",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = {
                        navController.navigate(Screen.Home) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )

                if (isAdmin) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Inventory, contentDescription = null) },
                        label = { Text("Productos") },
                        selected = false,
                        onClick = {
                            navController.navigate(Screen.AdminProductos) { launchSingleTop = true }
                            scope.launch { drawerState.close() }
                        }
                    )
                }

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Category, contentDescription = null) },
                    label = { Text("Categorías") },
                    selected = false,
                    onClick = {
                        navController.navigate(Screen.Categorias) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    label = { Text("Carrito") },
                    selected = false,
                    onClick = {
                        navController.navigate(Screen.Carrito) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                    label = { Text(if (isAdmin) "Todos los Pedidos" else "Mis Pedidos") },
                    selected = false,
                    onClick = {
                        if (isAdmin) {
                            navController.navigate(Screen.AdminPedidos) { launchSingleTop = true }
                        } else {
                            navController.navigate(Screen.MisPedidos) { launchSingleTop = true }
                        }
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = {
                        navController.navigate(Screen.Perfil) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )

                Spacer(Modifier.weight(1f))
                HorizontalDivider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                    label = { Text("Cerrar Sesión") },
                    selected = false,
                    onClick = {
                        authViewModel.processIntent(AuthIntent.SignOut)
                        scope.launch { drawerState.close() }
                    }
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("AlquilaFest") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home,
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
                        onCategoriaClick = { id, nombre ->
                            if (isAdmin) {
                                navController.navigate(Screen.EditCategoria(id))
                            } else {
                                navController.navigate(Screen.ProductosPorCategoria(id, nombre))
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

                composable<Screen.ProductosPorCategoria> {
                    val args = it.toRoute<Screen.ProductosPorCategoria>()
                    ProductosPorCategoriaScreen(
                        categoriaId = args.categoriaId,
                        categoriaNombre = args.categoriaNombre,
                        onProductoClick = { id ->
                            navController.navigate(Screen.ProductoDetail(id))
                        },
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
}