package com.utp.avance2_proyectofinal.repository

import android.content.Context
import com.utp.avance2_proyectofinal.data.HistorialResiduos
import com.utp.avance2_proyectofinal.data.RegistrarResiduos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImpactoAmbientalRepository(contexto: Context) {

    private val db = HistorialResiduos(contexto)

    suspend fun obtenerTodos(): List<RegistrarResiduos> =
        withContext(Dispatchers.IO) { db.obtenerTodos() }
}