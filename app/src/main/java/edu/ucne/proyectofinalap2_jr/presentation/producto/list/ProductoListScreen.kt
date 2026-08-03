package edu.ucne.proyectofinalap2_jr.presentation.producto.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import edu.ucne.proyectofinalap2_jr.domain.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoListScreen(
    viewModel: ProductoListViewModel = hiltViewModel(),
    onProductoClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    onEditClick: (String) -> Unit,
    isAdmin: Boolean = false
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ProductoListBodyScreen(
        state = state,
        onProductoClick = onProductoClick,
        onCreateClick = onCreateClick,
        onEditClick = onEditClick,
        onDeleteClick = viewModel::delete,
        isAdmin = isAdmin
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoListBodyScreen(
    state: ProductoListUiState,
    onProductoClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    isAdmin: Boolean = false
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Productos") })
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(onClick = onCreateClick) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
                state.error != null -> Text(
                    "Error: ${state.error}",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.productos) { producto ->
                        ProductoListItem(
                            producto = producto,
                            isAdmin = isAdmin,
                            onClick = { onProductoClick(producto.productoId) },
                            onEdit = { onEditClick(producto.productoId) },
                            onDelete = { onDeleteClick(producto.productoId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoListItem(
    producto: Producto,
    isAdmin: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = producto.imagen,
                contentDescription = producto.nombre,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold)
                Text(
                    producto.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )
                Text(
                    "$${String.format("%,.2f", producto.precio)}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (isAdmin) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductoListBodyScreenPreview() {
    MaterialTheme {
        ProductoListBodyScreen(
            state = ProductoListUiState(),
            onProductoClick = {},
            onCreateClick = {},
            onEditClick = {},
            onDeleteClick = {},
            isAdmin = false
        )
    }
}