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
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import edu.ucne.proyectofinalap2_jr.presentation.auth.AuthIntent
import edu.ucne.proyectofinalap2_jr.presentation.auth.AuthViewModel
import edu.ucne.proyectofinalap2_jr.presentation.auth.LoginScreen
import edu.ucne.proyectofinalap2_jr.presentation.carrito.CarritoScreen
import edu.ucne.proyectofinalap2_jr.presentation.carrito.CarritoViewModel
import edu.ucne.proyectofinalap2_jr.presentation.categoria.CategoriaListScreen
import edu.ucne.proyectofinalap2_jr.presentation.categoria.CreateCategoriaScreen
import edu.ucne.proyectofinalap2_jr.presentation.categoria.EditCategoriaScreen
import edu.ucne.proyectofinalap2_jr.presentation.home.HomeScreen
import edu.ucne.proyectofinalap2_jr.presentation.pedido.AdminPedidosScreen
import edu.ucne.proyectofinalap2_jr.presentation.pedido.DetallePedidoScreen
import edu.ucne.proyectofinalap2_jr.presentation.pedido.MisPedidosScreen
import edu.ucne.proyectofinalap2_jr.presentation.perfil.PerfilScreen
import edu.ucne.proyectofinalap2_jr.presentation.producto.create.CreateProductoScreen
import edu.ucne.proyectofinalap2_jr.presentation.producto.detail.ProductoDetailScreen
import edu.ucne.proyectofinalap2_jr.presentation.producto.list.ProductoListScreen
import edu.ucne.proyectofinalap2_jr.presentation.producto.list.ProductosPorCategoriaScreen
import edu.ucne.proyectofinalap2_jr.presentation.carrito.CheckoutScreen
import edu.ucne.proyectofinalap2_jr.presentation.producto.edit.EditProductoScreen
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationDisplay() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val carritoViewModel: CarritoViewModel = hiltViewModel()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isAdmin = authState.isAdmin

    val backStack = rememberNavBackStack(
        if (authState.user != null) Screen.Home else Screen.Login
    )

    LaunchedEffect(authState.user) {
        if (authState.user == null) {
            while (backStack.size > 1) {
                backStack.removeAt(backStack.size - 1)
            }
            backStack[0] = Screen.Login
        }
    }

    val showDrawer = backStack.lastOrNull() !is Screen.Login

    if (!showDrawer) {
        NavDisplay(
            backStack = backStack,
            entryProvider = entryProvider {
                entry<Screen.Login> {
                    LoginScreen(
                        onLoginSuccess = {
                            backStack.add(Screen.Home)
                        }
                    )
                }
            }
        )
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
                        backStack.add(Screen.Home)
                        scope.launch { drawerState.close() }
                    }
                )

                if (isAdmin) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Inventory, contentDescription = null) },
                        label = { Text("Productos") },
                        selected = false,
                        onClick = {
                            backStack.add(Screen.AdminProductos)
                            scope.launch { drawerState.close() }
                        }
                    )
                }

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Category, contentDescription = null) },
                    label = { Text("Categorías") },
                    selected = false,
                    onClick = {
                        backStack.add(Screen.Categorias)
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    label = { Text("Carrito") },
                    selected = false,
                    onClick = {
                        backStack.add(Screen.Carrito)
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                    label = { Text(if (isAdmin) "Todos los Pedidos" else "Mis Pedidos") },
                    selected = false,
                    onClick = {
                        if (isAdmin) {
                            backStack.add(Screen.AdminPedidos)
                        } else {
                            backStack.add(Screen.MisPedidos)
                        }
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = {
                        backStack.add(Screen.Perfil)
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
            NavDisplay(
                backStack = backStack,
                modifier = Modifier.padding(padding),
                entryProvider = entryProvider {

                    entry<Screen.Login> {
                        LoginScreen(
                            onLoginSuccess = {
                                backStack.add(Screen.Home)
                            }
                        )
                    }

                    entry<Screen.Home> {
                        HomeScreen(
                            onProductoClick = { id ->
                                backStack.add(Screen.ProductoDetail(id))
                            }
                        )
                    }

                    entry<Screen.Categorias> {
                        CategoriaListScreen(
                            onCreateClick = {
                                backStack.add(Screen.CreateCategoria)
                            },
                            onCategoriaClick = { id, nombre ->
                                if (isAdmin) {
                                    backStack.add(Screen.EditCategoria(id))
                                } else {
                                    backStack.add(Screen.ProductosPorCategoria(id, nombre))
                                }
                            },
                            isAdmin = isAdmin
                        )
                    }

                    entry<Screen.CreateCategoria> {
                        CreateCategoriaScreen(
                            onBack = {
                                if (backStack.isNotEmpty())
                                    backStack.removeAt(backStack.size - 1)
                            }
                        )
                    }

                    entry<Screen.EditCategoria> { key ->
                        EditCategoriaScreen(
                            categoriaId = key.categoriaId,
                            onBack = {
                                if (backStack.isNotEmpty())
                                    backStack.removeAt(backStack.size - 1)
                            }
                        )
                    }

                    entry<Screen.ProductosPorCategoria> { key ->
                        ProductosPorCategoriaScreen(
                            categoriaId = key.categoriaId,
                            categoriaNombre = key.categoriaNombre,
                            onProductoClick = { id ->
                                backStack.add(Screen.ProductoDetail(id))
                            },
                            onBack = {
                                if (backStack.isNotEmpty())
                                    backStack.removeAt(backStack.size - 1)
                            }
                        )
                    }

                    entry<Screen.AdminProductos> {
                        ProductoListScreen(
                            onProductoClick = { id ->
                                if (isAdmin) {
                                    backStack.add(Screen.EditProducto(id))
                                } else {
                                    backStack.add(Screen.ProductoDetail(id))
                                }
                            },
                            onCreateClick = {
                                backStack.add(Screen.ProductoCreate())
                            },
                            isAdmin = isAdmin
                        )
                    }
                    entry<Screen.EditProducto> { key ->
                        EditProductoScreen(
                            productoId = key.productoId,
                            onBack = {
                                if (backStack.isNotEmpty())
                                    backStack.removeAt(backStack.size - 1)
                            }
                        )
                    }

                    entry<Screen.ProductoDetail> { key ->
                        ProductoDetailScreen(
                            productoId = key.productoId,
                            onBack = {
                                if (backStack.isNotEmpty())
                                    backStack.removeAt(backStack.size - 1)
                            },
                            onAgregarAlCarrito = { item ->
                                carritoViewModel.agregarItem(item)
                                if (backStack.isNotEmpty())
                                    backStack.removeAt(backStack.size - 1)
                            }
                        )
                    }

                    entry<Screen.ProductoCreate> { key ->
                        CreateProductoScreen(
                            productoId = key.productoId,
                            onBack = {
                                if (backStack.isNotEmpty())
                                    backStack.removeAt(backStack.size - 1)
                            }
                        )
                    }

                    entry<Screen.MisPedidos> {
                        MisPedidosScreen(
                            onPedidoClick = { id ->
                                backStack.add(Screen.DetallePedido(id))
                            }
                        )
                    }

                    entry<Screen.DetallePedido> { key ->
                        DetallePedidoScreen(
                            pedidoId = key.pedidoId,
                            onBack = {
                                if (backStack.isNotEmpty())
                                    backStack.removeAt(backStack.size - 1)
                            }
                        )
                    }

                    entry<Screen.AdminPedidos> {
                        AdminPedidosScreen(
                            onPedidoClick = { id ->
                                backStack.add(Screen.DetallePedido(id))
                            }
                        )
                    }

                    entry<Screen.Perfil> {
                        PerfilScreen(
                            onSignOut = {
                                authViewModel.processIntent(AuthIntent.SignOut)
                            }
                        )
                    }
                    entry<Screen.Carrito> {
                        CarritoScreen(
                            viewModel = carritoViewModel,
                            onPedidoExitoso = {
                                backStack.add(Screen.MisPedidos)
                            },
                            onContinuarAlPago = { carritoState ->
                                backStack.add(Screen.Checkout)
                            }
                        )
                    }
                    entry<Screen.Checkout> {
                        CheckoutScreen(
                            carritoState = carritoViewModel.state.collectAsState().value,
                            onBack = {
                                if (backStack.isNotEmpty())
                                    backStack.removeAt(backStack.size - 1)
                            },
                            onPedidoExitoso = {
                                carritoViewModel.limpiarCarrito()
                                backStack.add(Screen.MisPedidos)
                            }
                        )
                    }
                }
            )
        }
    }
}