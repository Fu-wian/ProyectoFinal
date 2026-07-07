package com.utp.avance2_proyectofinal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.utp.avance2_proyectofinal.data.EcoSpot
import com.utp.avance2_proyectofinal.repository.EcoSpotRepository

class EcoSpotVM(application: Application) : AndroidViewModel(application) {

    private val repository = EcoSpotRepository(application)

    val listaEcoSpots = MutableLiveData<List<EcoSpot>>()

    fun cargarEcoSpots() {
        val puntos = repository.obtenerEcoSpots()
        listaEcoSpots.value = puntos
    }

    fun agregarEcoSpot(
        nombre: String,
        descripcion: String,
        tipoResiduo: String,
        latitud: Double,
        longitud: Double
    ): Boolean {
        if (nombre.isBlank() || descripcion.isBlank() || tipoResiduo.isBlank()) {
            return false
        }

        val ecoSpot = EcoSpot(
            nombre = nombre,
            descripcion = descripcion,
            tipoResiduo = tipoResiduo,
            latitud = latitud,
            longitud = longitud
        )

        val resultado = repository.insertarEcoSpot(ecoSpot)

        if (resultado != -1L) {
            cargarEcoSpots()
            return true
        }

        return false
    }

    fun eliminarEcoSpot(ecoSpot: EcoSpot): Boolean {
        if (ecoSpot.esPrecargado || ecoSpot.id <= 0) {
            return false
        }

        val filasEliminadas = repository.eliminarEcoSpot(ecoSpot.id)

        if (filasEliminadas > 0) {
            cargarEcoSpots()
            return true
        }

        return false
    }
}