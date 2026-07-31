package edu.ucne.proyectofinalap2_jr.presentation.login

import android.content.Context

sealed class AuthUiEvent {
    data class SignInWithGoogle(val context: Context) : AuthUiEvent()
    object SignOut : AuthUiEvent()
}