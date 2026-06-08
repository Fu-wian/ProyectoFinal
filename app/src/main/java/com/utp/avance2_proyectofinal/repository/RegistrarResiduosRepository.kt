package com.utp.avance2_proyectofinal.repository

import android.content.Context
import com.utp.avance2_proyectofinal.data.HistorialResiduos
import com.utp.avance2_proyectofinal.data.RegistrarResiduos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegistrarResiduosRepository(context: Context) {

    private val db = HistorialResiduos(context)

    suspend fun agregar(r: RegistrarResiduos): Long =
        withContext(Dispatchers.IO) { db.insertarResiduo(r) }

    suspend fun obtenerTodos(filtro: String? = null): List<RegistrarResiduos> =
        withContext(Dispatchers.IO) { db.obtenerTodos(filtro) }

    suspend fun actualizar(r: RegistrarResiduos): Int =
        withContext(Dispatchers.IO) { db.actualizarResiduo(r) }

    suspend fun eliminar(id: Long): Int =
        withContext(Dispatchers.IO) { db.eliminarResiduo(id) }

    suspend fun contar(): Int =
        withContext(Dispatchers.IO) { db.contarResiduos() }
}