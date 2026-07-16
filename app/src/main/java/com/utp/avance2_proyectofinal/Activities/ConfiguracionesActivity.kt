package com.utp.avance2_proyectofinal.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import com.utp.avance2_proyectofinal.R

class ConfiguracionesActivity : BaseActivity() {

    override fun obtenerItemMenu() = R.id.nav_config

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configuraciones)

        findViewById<LinearLayout>(R.id.opcionPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.opcionAprender).setOnClickListener {
            startActivity(Intent(this, AprendeReciclarActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.opcionRecompensas).setOnClickListener {
            startActivity(Intent(this, RecompensasActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.opcionCampanias).setOnClickListener {
            startActivity(Intent(this, CampaniasActivity::class.java))
        }

    }
}