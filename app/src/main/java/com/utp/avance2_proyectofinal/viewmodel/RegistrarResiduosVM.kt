package com.utp.avance2_proyectofinal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.utp.avance2_proyectofinal.data.RegistrarResiduos
import com.utp.avance2_proyectofinal.repository.RegistrarResiduosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistrarResiduosVM (application: Application): AndroidViewModel(application){
    private val repository = RegistrarResiduosRepository(application)

    private val _guardado = MutableStateFlow(false)
    val guardado: StateFlow<Boolean> = _guardado.asStateFlow()

    fun agregarResiduo(
        categoria: String,
        cantidad: Double,
        unidad: String,
        origen: String,
        fecha: Date = Date()
    ) {
        val fechaStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(fecha)
        viewModelScope.launch {
            repository.agregar(
                RegistrarResiduos(
                    categoria = categoria,
                    cantidad  = cantidad,
                    unidad    = unidad,
                    origen    = origen,
                    fecha = fechaStr
                )
            )
            _guardado.value = true
        }

    }

    fun resetGuardado() {
        _guardado.value = false
    }
}