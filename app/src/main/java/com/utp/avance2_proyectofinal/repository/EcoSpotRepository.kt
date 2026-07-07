package com.utp.avance2_proyectofinal.repository

import android.content.Context
import com.utp.avance2_proyectofinal.data.EcoSpot
import com.utp.avance2_proyectofinal.data.EcoSpotDB

class EcoSpotRepository(context: Context) {

    private val ecoSpotDB = EcoSpotDB(context)

    private val puntosPrecargados = listOf(
        EcoSpot(
            id = -1,
            nombre = "Eco Spot UTP",
            descripcion = "Punto de reciclaje dentro o cercano a la Universidad Tecnológica de Panamá.",
            latitud = 9.0227,
            longitud = -79.5317,
            tipoResiduo = "General",
            esPrecargado = true
        ),
        EcoSpot(
            id = -2,
            nombre = "Eco Spot Multiplaza",
            descripcion = "Punto de reciclaje ubicado en una zona comercial de fácil acceso.",
            latitud = 9.0109,
            longitud = -79.4936,
            tipoResiduo = "Plástico, papel y cartón",
            esPrecargado = true
        ),
        EcoSpot(
            id = -3,
            nombre = "Eco Spot Parque Omar",
            descripcion = "Punto de reciclaje comunitario ubicado cerca del Parque Omar.",
            latitud = 9.0043,
            longitud = -79.5081,
            tipoResiduo = "General",
            esPrecargado = true
        )
    )

    fun insertarEcoSpot(ecoSpot: EcoSpot): Long {
        return ecoSpotDB.insertarEcoSpot(ecoSpot)
    }

    fun obtenerEcoSpotsUsuario(): List<EcoSpot> {
        return ecoSpotDB.obtenerEcoSpots()
    }

    fun obtenerEcoSpots(): List<EcoSpot> {
        val puntosUsuario = ecoSpotDB.obtenerEcoSpots()
        return puntosPrecargados + puntosUsuario
    }

    fun eliminarEcoSpot(id: Long): Int {
        return ecoSpotDB.eliminarEcoSpot(id)
    }
}