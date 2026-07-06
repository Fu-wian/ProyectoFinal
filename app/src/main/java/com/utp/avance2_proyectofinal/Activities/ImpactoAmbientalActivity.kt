package com.utp.avance2_proyectofinal.Activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.utp.avance2_proyectofinal.R
import com.utp.avance2_proyectofinal.viewmodel.ImpactoAmbientalVM
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

class ImpactoAmbientalActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOMBRE_USUARIO = "extra_Impacto"
        private const val ALTURA_MAX_DP = 130f
        private const val CO2_POR_KM_AUTO = 0.2   // kg de CO2 por km de auto promedio
    }

    private val viewModel: ImpactoAmbientalVM by viewModels()

    private lateinit var tvSemana: TextView
    private lateinit var tvVacio: TextView
    private lateinit var contenido: LinearLayout
    private lateinit var tvCantidadResiduos: TextView
    private lateinit var tvComparacion: TextView
    private lateinit var tvCantidadCo2: TextView
    private lateinit var tvEquivalenciaCo2: TextView
    private lateinit var tvCantidadReciclada: TextView
    private lateinit var tvDetalleReciclado: TextView
    private lateinit var layoutCategorias: LinearLayout
    private lateinit var barras: List<View>
    private lateinit var valoresBarras: List<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tu_impacto_ambiental)

        tvSemana            = findViewById(R.id.tvSemana)
        tvVacio             = findViewById(R.id.tvVacio)
        contenido           = findViewById(R.id.contenido)
        tvCantidadResiduos  = findViewById(R.id.tvCantidadResiduos)
        tvComparacion       = findViewById(R.id.tvComparacion)
        tvCantidadCo2       = findViewById(R.id.tvcantidadCo2)
        tvEquivalenciaCo2   = findViewById(R.id.tvEquivalenciaCo2)
        tvCantidadReciclada = findViewById(R.id.tvcantidadReciclada)
        tvDetalleReciclado  = findViewById(R.id.tvDetalleReciclado)
        layoutCategorias    = findViewById(R.id.layoutCategorias)

        barras = listOf(
            findViewById(R.id.barLun), findViewById(R.id.barMar),
            findViewById(R.id.barMie), findViewById(R.id.barJue),
            findViewById(R.id.barVie), findViewById(R.id.barSab),
            findViewById(R.id.barDom)
        )
        valoresBarras = listOf(
            findViewById(R.id.tvValLun), findViewById(R.id.tvValMar),
            findViewById(R.id.tvValMie), findViewById(R.id.tvValJue),
            findViewById(R.id.tvValVie), findViewById(R.id.tvValSab),
            findViewById(R.id.tvValDom)
        )

        findViewById<Button>(R.id.btnVolver).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnAgregarResiduos).setOnClickListener {
            startActivity(Intent(this, RegistrarResiduosActivity::class.java))
        }

        observarViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarImpacto()
    }

    private fun observarViewModel() {
        lifecycleScope.launch {
            viewModel.impacto.collect { imp ->
                tvSemana.text = "Semana del ${imp.rangoSemana}"

                val hayDatos = imp.totalKg > 0
                tvVacio.visibility   = if (hayDatos) View.GONE else View.VISIBLE
                contenido.visibility = if (hayDatos) View.VISIBLE else View.GONE
                if (!hayDatos) return@collect

                tvCantidadResiduos.text = formatoKg(imp.totalKg)
                tvComparacion.text = textoComparacion(imp.totalKg, imp.totalSemanaPasadaKg)

                tvCantidadCo2.text = formatoKg(imp.co2EvitadoKg)
                val km = (imp.co2EvitadoKg / CO2_POR_KM_AUTO).roundToInt()
                tvEquivalenciaCo2.text = "como recorrer $km km en auto"

                val arboles = (imp.co2EvitadoKg / 21.0)   // un árbol absorbe ~21 kg de CO2 al año
                tvCantidadReciclada.text = String.format(Locale.getDefault(), "%.1f", arboles)
                tvDetalleReciclado.text = "árboles plantados (equiv. anual)"

                actualizarGrafica(imp.porDia)
                mostrarCategorias(imp.porCategoria, imp.totalKg)
            }
        }
    }

    private fun textoComparacion(actual: Double, pasada: Double): String {
        if (pasada == 0.0) return "Primera semana con registros"
        val dif = actual - pasada
        return when {
            dif > 0 -> "¡${formatoKg(dif)} más que la semana pasada!"
            dif < 0 -> "${formatoKg(-dif)} menos que la semana pasada"
            else    -> "Igual que la semana pasada"
        }
    }

    private fun actualizarGrafica(porDia: List<Double>) {
        val maximo = porDia.maxOrNull()?.takeIf { it > 0 } ?: return
        val densidad = resources.displayMetrics.density

        barras.forEachIndexed { i, barra ->
            val kg = porDia[i]
            val alturaDp = if (kg == 0.0) 3f
            else (kg / maximo * ALTURA_MAX_DP).toFloat().coerceAtLeast(4f)
            barra.layoutParams = barra.layoutParams.apply {
                height = (alturaDp * densidad).toInt()
            }
            barra.requestLayout()

            valoresBarras[i].text =
                if (kg == 0.0) "0"
                else String.format(Locale.getDefault(), "%.1f", kg)
        }
    }

    private fun mostrarCategorias(porCategoria: Map<String, Double>, totalKg: Double) {
        layoutCategorias.removeAllViews()
        val densidad = resources.displayMetrics.density

        porCategoria.entries.sortedByDescending { it.value }.forEach { (categoria, kg) ->
            val fila = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, (4 * densidad).toInt(), 0, (4 * densidad).toInt())
            }

            fila.addView(TextView(this).apply {
                text = categoria
                textSize = 12f
                layoutParams = LinearLayout.LayoutParams(
                    (90 * densidad).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
                )
            })

            fila.addView(ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
                max = 100
                progress = (kg / totalKg * 100).roundToInt()
                layoutParams = LinearLayout.LayoutParams(
                    0, (10 * densidad).toInt(), 1f
                )
            })

            fila.addView(TextView(this).apply {
                text = formatoKg(kg)
                textSize = 12f
                gravity = Gravity.END
                setTextColor(Color.parseColor("#757575"))
                layoutParams = LinearLayout.LayoutParams(
                    (60 * densidad).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
                )
            })

            layoutCategorias.addView(fila)
        }
    }

    private fun formatoKg(valor: Double): String =
        String.format(Locale.getDefault(), "%.1f kg", valor)
}