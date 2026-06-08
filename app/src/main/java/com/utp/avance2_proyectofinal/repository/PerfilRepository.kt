package com.utp.avance2_proyectofinal.repository

import android.content.Context
import com.utp.avance2_proyectofinal.data.PerfilUsuario

class PerfilRepository(context: Context) {

    private val prefs = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "perfil_prefs"

        private const val KEY_NOMBRE = "nombre"
        private const val KEY_CORREO = "correo"
        private const val KEY_TEMA = "tema"
        private const val KEY_NOTIFICACIONES = "notificaciones"
    }

    fun cargarPerfil(): PerfilUsuario {
        val nombre = prefs.getString(KEY_NOMBRE, "") ?: ""
        val correo = prefs.getString(KEY_CORREO, "") ?: ""
        val tema = prefs.getString(KEY_TEMA, "Claro") ?: "Claro"
        val notificaciones = prefs.getBoolean(KEY_NOTIFICACIONES, false)

        return PerfilUsuario(
            nombre = nombre,
            correo = correo,
            tema = tema,
            notificaciones = notificaciones
        )
    }

    fun guardarPerfil(perfil: PerfilUsuario) {
        prefs.edit()
            .putString(KEY_NOMBRE, perfil.nombre)
            .putString(KEY_CORREO, perfil.correo)
            .putString(KEY_TEMA, perfil.tema)
            .putBoolean(KEY_NOTIFICACIONES, perfil.notificaciones)
            .apply()
    }

}