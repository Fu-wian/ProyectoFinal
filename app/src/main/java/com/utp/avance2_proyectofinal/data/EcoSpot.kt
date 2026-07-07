package com.utp.avance2_proyectofinal.data

data class EcoSpot(
    val nombre: String,
    val descripcion: String,
    val latitud: Double,
    val longitud: Double,
    val tipoResiduo: String = "General",
    val esPrecargado: Boolean = false,
    val id: Long = 0
)