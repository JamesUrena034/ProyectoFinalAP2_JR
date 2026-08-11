package edu.ucne.proyectofinalap2_jr.presentation.producto.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import edu.ucne.proyectofinalap2_jr.domain.model.Categoria

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductoScreen(
    productoId: String = "",
    onBack: () -> Unit,
    viewModel: CreateProductoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(productoId) {
        viewModel.load(productoId)
    }

    LaunchedEffect(state.savedSuccessfully) {
        if (state.savedSuccessfully) onBack()
    }

    CreateProductoBodyScreen(
        state = state,
        onBack = onBack,
        onNombreChange = viewModel::onNombreChange,
        onDescripcionChange = viewModel::onDescripcionChange,
        onPrecioChange = viewModel::onPrecioChange,
        onImagenChange = viewModel::onImagenChange,
        onCategoriaSelected = viewModel::onCategoriaSelected,
        onStockChange = viewModel::onStockChange,
        onSave = viewModel::save
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductoBodyScreen(
    state: CreateProductoUiState,
    onBack: () -> Unit,
    onNombreChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onPrecioChange: (String) -> Unit,
    onImagenChange: (String) -> Unit,
    onCategoriaSelected: (Categoria) -> Unit,
    onStockChange: (String) -> Unit,
    onSave: () -> Unit
) {
    var expandedCategoria by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (state.productoId.isBlank()) "Nuevo Producto" else "Editar Producto")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (state.imagen.isNotBlank()) {
                        AsyncImage(
                            model = state.imagen,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    OutlinedTextField(
                        value = state.nombre,
                        onValueChange = onNombreChange,
                        label = { Text("Nombre") },
                        isError = state.nombreError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.nombreError?.let {
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    OutlinedTextField(
                        value = state.descripcion,
                        onValueChange = onDescripcionChange,
                        label = { Text("Descripción") },
                        isError = state.descripcionError != null,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    state.descripcionError?.let {
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    OutlinedTextField(
                        value = if (state.precio == 0.0) "" else state.precio.toString(),
                        onValueChange = onPrecioChange,
                        label = { Text("Precio") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = state.precioError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.precioError?.let {
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    OutlinedTextField(
                        value = if (state.stock == 0) "" else state.stock.toString(),
                        onValueChange = onStockChange,
                        label = { Text("Stock disponible") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = state.stockError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.stockError?.let {
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    OutlinedTextField(
                        value = state.imagen,
                        onValueChange = onImagenChange,
                        label = { Text("URL de imagen") },
                        isError = state.imagenError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.imagenError?.let {
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    ExposedDropdownMenuBox(
                        expanded = expandedCategoria,
                        onExpandedChange = { expandedCategoria = it }
                    ) {
                        OutlinedTextField(
                            value = state.categoriaNombre.ifBlank { "Selecciona una categoría" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            isError = state.categoriaError != null,
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategoria,
                            onDismissRequest = { expandedCategoria = false }
                        ) {
                            state.categorias.forEach { categoria ->
                                DropdownMenuItem(
                                    text = { Text(categoria.nombre) },
                                    onClick = {
                                        onCategoriaSelected(categoria)
                                        expandedCategoria = false
                                    }
                                )
                            }
                        }
                    }
                    state.categoriaError?.let {
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    if (state.error != null) {
                        Text(
                            "Error: ${state.error}",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = onSave,
                        enabled = !state.isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(if (state.productoId.isBlank()) "Guardar" else "Actualizar")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateProductoBodyScreenPreview() {
    MaterialTheme {
        CreateProductoBodyScreen(
            state = CreateProductoUiState(),
            onBack = {},
            onNombreChange = {},
            onDescripcionChange = {},
            onPrecioChange = {},
            onImagenChange = {},
            onCategoriaSelected = {},
            onStockChange = {},
            onSave = {}
        )
    }
}