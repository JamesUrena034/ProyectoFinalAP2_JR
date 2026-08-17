package edu.ucne.proyectofinalap2_jr.presentation.categoria

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoriaScreen(
    categoriaId: String,
    onBack: () -> Unit,
    viewModel: EditCategoriaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(categoriaId) {
        viewModel.load(categoriaId)
    }

    LaunchedEffect(state.savedSuccessfully) {
        if (state.savedSuccessfully) onBack()
    }

    EditCategoriaBodyScreen(
        state = state,
        onBack = onBack,
        onNombreChange = viewModel::onNombreChange,
        onDescripcionChange = viewModel::onDescripcionChange,
        onImagenChange = viewModel::onImagenChange,
        onSave = viewModel::save
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoriaBodyScreen(
    state: EditCategoriaUiState,
    onBack: () -> Unit,
    onNombreChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onImagenChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Categoría") },
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
                                .height(180.dp),
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
                        value = state.imagen,
                        onValueChange = onImagenChange,
                        label = { Text("URL de imagen") },
                        isError = state.imagenError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.imagenError?.let {
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
                            Text("Actualizar")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditCategoriaBodyScreenPreview() {
    MaterialTheme {
        EditCategoriaBodyScreen(
            state = EditCategoriaUiState(),
            onBack = {},
            onNombreChange = {},
            onDescripcionChange = {},
            onImagenChange = {},
            onSave = {}
        )
    }
}