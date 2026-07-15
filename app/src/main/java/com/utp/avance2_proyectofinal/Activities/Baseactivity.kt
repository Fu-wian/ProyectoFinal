package com.utp.avance2_proyectofinal.Activities

import android.content.Intent
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.utp.avance2_proyectofinal.R

/**
 * Clase base para todas las pantallas con barra de navegación inferior.
 *
 * Cómo funciona: intercepta el setContentView() que cada Activity ya llama
 * en su onCreate, infla primero activity_base.xml (que tiene el contenedor
 * y la barra) y mete el layout de la pantalla dentro del contenedor.
 * Así ninguna Activity necesita cambiar su layout ni su onCreate.
 */
abstract class BaseActivity : AppCompatActivity() {

    /** Cada pantalla indica cuál item del menú le corresponde */
    protected abstract fun obtenerItemMenu(): Int

    override fun setContentView(layoutResID: Int) {
        val vistaBase = layoutInflater.inflate(R.layout.activity_base, null)
        val contenedor = vistaBase.findViewById<FrameLayout>(R.id.contenedorBase)
        layoutInflater.inflate(layoutResID, contenedor, true)
        super.setContentView(vistaBase)

        configurarBarraNavegacion()
    }

    private fun configurarBarraNavegacion() {
        val barra = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Marca el item de la pantalla actual ANTES de poner el listener,
        // para que no se dispare una navegación al abrir la pantalla
        barra.selectedItemId = obtenerItemMenu()

        barra.setOnItemSelectedListener { item ->
            // Si ya estamos en esa pantalla, no hacemos nada
            if (item.itemId == obtenerItemMenu()) {
                return@setOnItemSelectedListener true
            }

            val destino = when (item.itemId) {
                R.id.nav_inicio    -> ImpactoAmbientalActivity::class.java
                R.id.nav_registrar -> RegistrarResiduosActivity::class.java
                R.id.nav_historial -> HistorialResiduosActivity::class.java
                R.id.nav_ecospots  -> EcoSpotActivity::class.java
                R.id.nav_config -> ConfiguracionesActivity::class.java
                else               -> return@setOnItemSelectedListener false
            }

            val intent = Intent(this, destino)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            overridePendingTransition(0, 0) // sin animación, se siente como cambiar de pestaña
            finish()
            true
        }
    }
}