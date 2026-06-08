package com.utp.avance2_proyectofinal.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.utp.avance2_proyectofinal.R


class PerfilActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_NOMBRE_USUARIO = "Extra_Perfil"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.perfil)
        val btnVolver = findViewById<Button>(R.id.button3)

        btnVolver.setOnClickListener {
            val intent2 = Intent(this, MainActivity::class.java)
            intent2.putExtra(MainActivity.EXTRA_NOMBRE_USUARIO,"Extra_Main")
            startActivity(intent2)
        }
    }
}