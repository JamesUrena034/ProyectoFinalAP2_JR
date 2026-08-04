package edu.ucne.proyectofinalap2_jr.presentation.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import edu.ucne.proyectofinalap2_jr.domain.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel = hiltViewModel(),
    onSignOut: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PerfilBodyScreen(
        state = state,
        onSignOut = {
            viewModel.signOut()
            onSignOut()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilBodyScreen(
    state: PerfilUiState,
    onSignOut: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Mi Perfil") })
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
                state.usuario != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (state.usuario.foto.isNotBlank()) {
                            AsyncImage(
                                model = state.usuario.foto,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Surface(
                                modifier = Modifier.size(100.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = state.usuario.nombre.firstOrNull()?.toString() ?: "U",
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Text(
                            state.usuario.nombre,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            state.usuario.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Surface(
                            color = if (state.usuario.rol == "admin")
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = state.usuario.rol.uppercase(),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (state.usuario.rol == "admin")
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = onSignOut,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Cerrar Sesión")
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("No se pudo cargar el perfil", color = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = onSignOut,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Cerrar Sesión")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilBodyScreenPreview() {
    MaterialTheme {
        PerfilBodyScreen(
            state = PerfilUiState(
                usuario = Usuario(
                    uid = "1",
                    nombre = "Victor Marino",
                    email = "victormarino1962@gmail.com",
                    foto = "",
                    rol = "admin"
                )
            ),
            onSignOut = {}
        )
    }
}