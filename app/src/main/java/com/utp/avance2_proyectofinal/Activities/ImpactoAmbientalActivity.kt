package com.utp.avance2_proyectofinal.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.utp.avance2_proyectofinal.R
import com.utp.avance2_proyectofinal.viewmodel.ImpactoAmbientalVM
import kotlinx.coroutines.launch
import java.util.Locale

class ImpactoAmbientalActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOMBRE_USUARIO = "extra_Impacto"
        private const val ALTURA_MAX_DP = 170f
    }

    private val viewModel: ImpactoAmbientalVM by viewModels()

    private lateinit var tvCantidadResiduos: TextView
    private lateinit var tvCantidadCo2: TextView
    private lateinit var tvCantidadReciclada: TextView
    private lateinit var barras: List<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tu_impacto_ambiental)

        tvCantidadResiduos  = findViewById(R.id.tvCantidadResiduos)
        tvCantidadCo2       = findViewById(R.id.tvcantidadCo2)
        tvCantidadReciclada = findViewById(R.id.tvcantidadReciclada)

        barras = listOf(
            findViewById(R.id.barLun), findViewById(R.id.barMar),
            findViewById(R.id.barMie), findViewById(R.id.barJue),
            findViewById(R.id.barVie), findViewById(R.id.barSab),
            findViewById(R.id.barDom)
        )

        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnAgregarResiduos).setOnClickListener {
            startActivity(Intent(this, RegistrarResiduosActivity::class.java))
        }

        observarViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarImpacto()   // refresca al volver de Registrar Residuos
    }

    private fun observarViewModel() {
        lifecycleScope.launch {
            viewModel.impacto.collect { imp ->
                tvCantidadResiduos.text  = String.format(Locale.getDefault(), "%.2f kg de residuos", imp.totalKg)
                tvCantidadCo2.text       = String.format(Locale.getDefault(), "%.2f kg", imp.co2Kg)
                tvCantidadReciclada.text = String.format(Locale.getDefault(), "%.2f kg", imp.recicladoKg)
                actualizarGrafica(imp.porDia)
            }
        }
    }

    private fun actualizarGrafica(porDia: List<Double>) {
        val maximo = porDia.maxOrNull()?.takeIf { it > 0 }
        val densidad = resources.displayMetrics.density
        barras.forEachIndexed { i, barra ->
            val alturaDp = if (maximo == null) 2f
            else (porDia[i] / maximo * ALTURA_MAX_DP).toFloat().coerceAtLeast(2f)
            barra.layoutParams = barra.layoutParams.apply {
                height = (alturaDp * densidad).toInt()
            }
            barra.requestLayout()
        }
    }
}