package com.utp.avance2_proyectofinal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.utp.avance2_proyectofinal.data.RegistrarResiduos
import com.utp.avance2_proyectofinal.repository.HistorialResiduosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistorialResiduosVM(application: Application) : AndroidViewModel(application){
    private val repository = HistorialResiduosRepository(application)

    private val _residuos = MutableStateFlow<List<RegistrarResiduos>>(emptyList())
    val residuos: StateFlow<List<RegistrarResiduos>> = _residuos.asStateFlow()

    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount.asStateFlow()

    init { cargarResiduos() }

    fun cargarResiduos(filtro: String? = null) {
        viewModelScope.launch {
            _residuos.value    = repository.obtenerTodos(filtro)
            _totalCount.value  = repository.contar()
        }
    }

    fun eliminarResiduo(id: Long) {
        viewModelScope.launch {
            repository.eliminar(id)
            cargarResiduos()
        }
    }
    fun editarResiduo(residuo: RegistrarResiduos) {
        viewModelScope.launch {
            repository.actualizar(residuo)
            cargarResiduos()
        }
    }
}