package com.utp.avance2_proyectofinal.data

data class RegistrarResiduos(
    val id: Long = 0L,
    val categoria: String,
    val cantidad: Double,
    val unidad: String,
    val origen: String,
    val fecha: String,
    val fotoUri: String? = null
)