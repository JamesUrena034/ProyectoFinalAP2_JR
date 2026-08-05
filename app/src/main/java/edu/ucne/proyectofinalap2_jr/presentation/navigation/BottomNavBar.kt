package edu.ucne.proyectofinalap2_jr.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val screen: Screen
)

@Composable
fun BottomNavBar(
    currentScreen: Screen,
    onNavClick: (Screen) -> Unit,
    isAdmin: Boolean = false
) {
    val items = if (isAdmin) {
        listOf(
            BottomNavItem("Inicio", Icons.Default.Home, Screen.Home),
            BottomNavItem("Productos", Icons.Default.Inventory, Screen.AdminProductos),
            BottomNavItem("Categorías", Icons.Default.Category, Screen.Categorias),
            BottomNavItem("Carrito", Icons.Default.ShoppingCart, Screen.Carrito),
            BottomNavItem("Pedidos", Icons.Default.Receipt, Screen.AdminPedidos),
            BottomNavItem("Perfil", Icons.Default.Person, Screen.Perfil)
        )
    } else {
        listOf(
            BottomNavItem("Inicio", Icons.Default.Home, Screen.Home),
            BottomNavItem("Categorías", Icons.Default.Category, Screen.Categorias),
            BottomNavItem("Carrito", Icons.Default.ShoppingCart, Screen.Carrito),
            BottomNavItem("Pedidos", Icons.Default.Receipt, Screen.MisPedidos),
            BottomNavItem("Perfil", Icons.Default.Person, Screen.Perfil)
        )
    }

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentScreen == item.screen,
                onClick = { onNavClick(item.screen) }
            )
        }
    }
}