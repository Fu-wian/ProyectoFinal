package com.utp.avance2_proyectofinal.Activities

import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.utp.avance2_proyectofinal.R
import com.utp.avance2_proyectofinal.viewmodel.RecompensasVM
import kotlinx.coroutines.launch
import java.util.Locale

class RecompensasActivity : AppCompatActivity() {   // hija: NO BaseActivity

    private val viewModel: RecompensasVM by viewModels()
    private lateinit var layoutPromos: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recompensas)

        layoutPromos = findViewById(R.id.layoutPromos)

        findViewById<Button>(R.id.btVolver).setOnClickListener { finish() }  // aquí SÍ es válido

        lifecycleScope.launch {
            viewModel.kgDelMes.collect { kg ->
                findViewById<TextView>(R.id.tvKgMes).text =
                    String.format(Locale.getDefault(), "%.1f kg", kg)
                mostrarPromos(kg)
            }
        }
    }

    private fun mostrarPromos(kgDelMes: Double) {
        layoutPromos.removeAllViews()

        viewModel.promos.forEach { promo ->
            val desbloqueada = kgDelMes >= promo.kgRequeridos

            val card = com.google.android.material.card.MaterialCardView(this).apply {
                radius = 16 * resources.displayMetrics.density
                cardElevation = 2 * resources.displayMetrics.density
                setCardBackgroundColor(android.graphics.Color.WHITE)
                alpha = if (desbloqueada) 1f else 0.5f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = (10 * resources.displayMetrics.density).toInt() }
            }

            val contenido = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                val pad = (16 * resources.displayMetrics.density).toInt()
                setPadding(pad, pad, pad, pad)
            }
            contenido.addView(TextView(this).apply {
                text = "${promo.titulo} — ${promo.socio}"
                textSize = 15f
                setTextColor(android.graphics.Color.parseColor("#1B5E20"))
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            })

            contenido.addView(TextView(this).apply {
                text = if (desbloqueada) "✅ Disponible"
                else "🔒 Te faltan ${String.format(Locale.getDefault(), "%.1f", promo.kgRequeridos - kgDelMes)} kg"
                textSize = 13f
                setTextColor(android.graphics.Color.parseColor("#66796B"))
            })
            val btnCanje = Button(this).apply {
                text = "Canjear →"
                setTextColor(Color.WHITE)
                backgroundTintList = android.content.res.ColorStateList.valueOf(
                    Color.parseColor("#2E7D32"))
                val pad = (4 * resources.displayMetrics.density).toInt()
                setPadding(pad, pad, pad, pad)
            }
            if (desbloqueada) {
                val btnCanje = Button(this).apply {
                    text = "Canjear →"
                    setTextColor(Color.WHITE)
                    backgroundTintList = android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#2E7D32"))
                }
                btnCanje.setOnClickListener {
                    startActivity(Intent(this, CanjeActivity::class.java).apply {
                        putExtra(CanjeActivity.EXTRA_TITULO,      promo.titulo)
                        putExtra(CanjeActivity.EXTRA_SOCIO,       promo.socio)
                        putExtra(CanjeActivity.EXTRA_DESCRIPCION, promo.descripcion)
                        putExtra(CanjeActivity.EXTRA_CODIGO,      promo.codigoCanje)
                        putExtra(CanjeActivity.EXTRA_INSTAGRAM,   promo.instagram)
                        putExtra(CanjeActivity.EXTRA_WEB,         promo.web)
                    })
                }
                contenido.addView(btnCanje)
            }

            card.addView(contenido)
            layoutPromos.addView(card)
        }
    }
}