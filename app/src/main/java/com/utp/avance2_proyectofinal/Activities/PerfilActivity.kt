package com.utp.avance2_proyectofinal.Activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.utp.avance2_proyectofinal.R
import com.utp.avance2_proyectofinal.viewmodel.PreferenciasVM
import kotlinx.coroutines.launch

class PerfilActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOMBRE_USUARIO = "Extra_perfil"
    }

    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var swNotificaciones: Switch
    private lateinit var btnGuardar: Button

    private lateinit var btnVolver: Button

    private val perfilViewModel: PreferenciasVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.perfil)

        inicializarVistas()
        observarPerfil()

        btnGuardar.setOnClickListener {
            guardarPerfil()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun inicializarVistas() {
        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        swNotificaciones = findViewById(R.id.swNotificaciones)
        btnGuardar = findViewById(R.id.btGuardar)
        btnVolver = findViewById(R.id.btVolver)
    }

    private fun observarPerfil() {
        lifecycleScope.launch {
            perfilViewModel.perfil.collect { perfil ->

                etNombre.setText(perfil.nombre)
                etCorreo.setText(perfil.correo)

                swNotificaciones.isChecked = perfil.notificaciones
            }
        }
    }

    private fun guardarPerfil() {
        val nombre = etNombre.text.toString().trim()
        val correo = etCorreo.text.toString().trim()

        //Validacion de errores
        if (nombre.isEmpty()) {
            etNombre.error = "Ingrese su nombre"
            return
        }

        if (correo.isEmpty()) {
            etCorreo.error = "Ingrese su correo"
            return
        }

        val notificaciones = swNotificaciones.isChecked

        perfilViewModel.guardarPerfil(
            nombre = nombre,
            correo = correo,
            notificaciones = notificaciones
        )

        Toast.makeText(
            this,
            "Perfil guardado correctamente",
            Toast.LENGTH_SHORT
        ).show()
    }
}