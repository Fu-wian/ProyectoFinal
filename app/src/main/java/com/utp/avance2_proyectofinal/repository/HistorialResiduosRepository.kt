package com.utp.avance2_proyectofinal.repository

import android.content.Context
import com.utp.avance2_proyectofinal.data.HistorialResiduos
import com.utp.avance2_proyectofinal.data.RegistrarResiduos

class HistorialResiduosRepository(context: Context) {

    private val db = HistorialResiduos(context)

    suspend fun obtenerTodos(filtro: String? = null): List<RegistrarResiduos> =
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            db.obtenerTodos(filtro)
        }

    suspend fun eliminar(id: Long): Int =
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            db.eliminarResiduo(id)
        }

    suspend fun contar(): Int =
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            db.contarResiduos()
        }

    suspend fun actualizar(r: RegistrarResiduos): Int =
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            db.actualizarResiduo(r)
        }
}