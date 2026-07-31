package edu.ucne.proyectofinalap2_jr.domain.model

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val foto: String = "",
    val rol: String = "usuario"
)