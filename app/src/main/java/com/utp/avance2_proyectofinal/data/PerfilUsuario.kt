package com.utp.avance2_proyectofinal.data

data class PerfilUsuario(
    val nombre: String = "",
    val correo: String = "",
    val tema: String = "Claro",
    val notificaciones: Boolean = false
)