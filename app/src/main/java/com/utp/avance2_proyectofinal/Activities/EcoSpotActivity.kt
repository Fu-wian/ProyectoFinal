package com.utp.avance2_proyectofinal.Activities

import android.content.Intent
import android.net.Uri // 1. IMPORTACIÓN CORRECTA PARA Uri.parse
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.utp.avance2_proyectofinal.R

class EcoSpotActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOMBRE_USUARIO = "extra_Ecospot"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eco_spots)

        val mapaBoton = findViewById<Button>(R.id.btnMapal)

        mapaBoton.setOnClickListener {
            val uri = Uri.parse("google.navigation:q=9.0152,-79.5312") // Coordenadas de ejemplo de la UTP, Panamá
            val intent = Intent(Intent.ACTION_VIEW, uri)

            // Forzar a que intente abrir directamente la app de Google Maps
            intent.setPackage("com.google.android.apps.maps")

            // 2. SE REMOVIÓ LA PRIMERA LLAMADA REPETIDA DE startActivity(intent)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                // Si el usuario deshabilitó Maps, intentamos abrir la versión web como respaldo
                val webUri = Uri.parse("https://www.google.com/maps")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                startActivity(webIntent)
            }
        }
    }
}