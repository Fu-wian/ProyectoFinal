package com.utp.avance2_proyectofinal.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.utp.avance2_proyectofinal.R


class RegistrarResiduoActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOMBRE_USUARIO = "extra_registrar"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrar_residuos)

        val nombre = intent.getStringExtra(EXTRA_NOMBRE_USUARIO)
    }
}