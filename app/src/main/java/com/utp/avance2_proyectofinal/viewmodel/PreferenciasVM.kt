package com.utp.avance2_proyectofinal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.utp.avance2_proyectofinal.data.PerfilUsuario
import com.utp.avance2_proyectofinal.repository.PerfilRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferenciasVM (application: Application) : AndroidViewModel(application) {

    private val repository = PerfilRepository(application)

    private val _perfil = MutableStateFlow(PerfilUsuario())
    val perfil: StateFlow<PerfilUsuario> = _perfil.asStateFlow()

    init {
        cargarPerfil()
    }

    fun cargarPerfil() {
        _perfil.value = repository.cargarPerfil()
    }

    fun guardarPerfil(
        nombre: String,
        correo: String,
        tema: String,
        notificaciones: Boolean
    ) {
        val nuevoPerfil = PerfilUsuario(
            nombre = nombre,
            correo = correo,
            tema = tema,
            notificaciones = notificaciones
        )

        repository.guardarPerfil(nuevoPerfil)
        _perfil.value = nuevoPerfil
    }
}