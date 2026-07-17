package com.utp.avance2_proyectofinal.data

data class Promo(
    val titulo: String,
    val socio: String,
    val descripcion: String,
    val kgRequeridos: Double,
    val codigoCanje: String,
    val instagram: String = "",
    val web: String = ""
)