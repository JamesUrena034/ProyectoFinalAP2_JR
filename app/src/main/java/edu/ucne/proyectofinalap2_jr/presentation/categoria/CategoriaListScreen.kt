package edu.ucne.proyectofinalap2_jr.presentation.categoria

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import edu.ucne.proyectofinalap2_jr.domain.model.Categoria

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaListScreen(
    viewModel: CategoriaListViewModel = hiltViewModel(),
    onCreateClick: () -> Unit,
    onCategoriaClick: (String, String) -> Unit,
    isAdmin: Boolean = false
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CategoriaListBodyScreen(
        state = state,
        onCreateClick = onCreateClick,
        onCategoriaClick = onCategoriaClick,
        onDeleteClick = viewModel::delete,
        isAdmin = isAdmin
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaListBodyScreen(
    state: CategoriaListUiState,
    onCreateClick: () -> Unit,
    onCategoriaClick: (String, String) -> Unit,
    onDeleteClick: (String) -> Unit,
    isAdmin: Boolean = false
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Categorías") })
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
                state.categorias.isEmpty() -> Text(
                    "No hay categorías",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.categorias) { categoria ->
                            CategoriaItem(
                                categoria = categoria,
                                isAdmin = isAdmin,
                                onClick = {
                                    onCategoriaClick(categoria.categoriaId, categoria.nombre)
                                },
                                onDelete = { onDeleteClick(categoria.categoriaId) }
                            )
                        }
                    }
                    HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Total categorías: ${state.categorias.size}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoriaItem(
    categoria: Categoria,
    isAdmin: Boolean,
    onClick: () -> Unit,
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
                model = categoria.imagen,
                contentDescription = categoria.nombre,
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(categoria.nombre, fontWeight = FontWeight.Bold)
                Text(
                    categoria.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            if (isAdmin) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoriaListBodyScreenPreview() {
    MaterialTheme {
        CategoriaListBodyScreen(
            state = CategoriaListUiState(),
            onCreateClick = {},
            onCategoriaClick = { _, _ -> },
            onDeleteClick = {},
            isAdmin = true
        )
    }
}