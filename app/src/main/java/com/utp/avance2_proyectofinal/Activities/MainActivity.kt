package com.utp.avance2_proyectofinal.Activities

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.utp.avance2_proyectofinal.R
import androidx.cardview.widget.CardView
import android.content.Intent
import com.utp.avance2_proyectofinal.data.RegistrarResiduos


class MainActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_NOMBRE_USUARIO = "Extra_Main"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_principal)

        val cardPerfil = findViewById<CardView>(R.id.CardPerfil)
        val cardHistorial = findViewById<CardView>(R.id.CardHistorial)
        val cardRegistrar = findViewById<CardView>(R.id.cardRegistrar)
        val cardImpacto = findViewById<CardView>(R.id.CardImpacto)
        val cardEcospot = findViewById<CardView>(R.id.EcoSpots)

        cardPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            intent.putExtra(PerfilActivity.EXTRA_NOMBRE_USUARIO,"Extra_Perfil")
            startActivity(intent)
        }

        cardHistorial.setOnClickListener {
            val intent = Intent(this, HistorialResiduosActivity::class.java)
            intent.putExtra(PerfilActivity.EXTRA_NOMBRE_USUARIO,"Extra_historial")
            startActivity(intent)
        }
        cardRegistrar.setOnClickListener {
            val intent = Intent(this, RegistrarResiduosActivity::class.java)
            intent.putExtra(PerfilActivity.EXTRA_NOMBRE_USUARIO,"Extra_Registrar")
            startActivity(intent)
        }
        cardImpacto.setOnClickListener {
            val intent = Intent(this, ImpactoAmbientalActivity::class.java)
            intent.putExtra(ImpactoAmbientalActivity.EXTRA_NOMBRE_USUARIO, "extra_Impacto")
            startActivity(intent)
        }
        cardEcospot.setOnClickListener {
            val intent = Intent(this, EcoSpotActivity::class.java)
            startActivity(intent)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}