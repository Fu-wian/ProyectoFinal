package com.utp.avance2_proyectofinal.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.utp.avance2_proyectofinal.R
import kotlin.jvm.Throws


class HistorialResiduosActivity : AppCompatActivity(){

    companion object {
        const val EXTRA_NOMBRE_USUARIO = "extra_ecospot"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.historial_residuos)
        val btnVolver = findViewById<Button>(R.id.Rbtnvolver)

        btnVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_NOMBRE_USUARIO,"Extra_main")
            startActivity(intent)
        }
    }
}