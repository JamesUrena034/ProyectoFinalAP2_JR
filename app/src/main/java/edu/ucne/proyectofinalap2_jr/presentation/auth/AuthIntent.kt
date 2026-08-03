package edu.ucne.proyectofinalap2_jr.presentation.auth

import android.content.Context

sealed class AuthIntent {
    data class SignInWithGoogle(val context: Context) : AuthIntent()
    data object SignOut : AuthIntent()
}