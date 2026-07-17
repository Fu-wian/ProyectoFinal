package com.utp.avance2_proyectofinal.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.utp.avance2_proyectofinal.R
import com.utp.avance2_proyectofinal.data.Promo
import com.utp.avance2_proyectofinal.viewmodel.RecompensasVM
import kotlinx.coroutines.launch
import java.util.Locale

class RecompensasActivity : AppCompatActivity() {

    private val viewModel: RecompensasVM by viewModels()
    private lateinit var listPromos: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recompensas)

        listPromos = findViewById(R.id.listPromos)
        findViewById<Button>(R.id.btVolver).setOnClickListener { finish() }

        lifecycleScope.launch {
            viewModel.kgDelMes.collect { kg ->
                findViewById<TextView>(R.id.tvKgMes).text =
                    String.format(Locale.getDefault(), "%.1f kg", kg)
                mostrarPromos(kg)
            }
        }
    }

    private fun mostrarPromos(kgDelMes: Double) {
        listPromos.adapter = object : ArrayAdapter<Promo>(
            this@RecompensasActivity, R.layout.item_promo, viewModel.promos
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.item_promo, parent, false)
                val promo = viewModel.promos[position]
                val desbloqueada = kgDelMes >= promo.kgRequeridos

                view.findViewById<TextView>(R.id.tvTituloPromo).text =
                    "${promo.titulo} — ${promo.socio}"

                view.findViewById<TextView>(R.id.tvEstadoPromo).text =
                    if (desbloqueada) "✅ Disponible"
                    else "🔒 Te faltan ${String.format(Locale.getDefault(), "%.1f", promo.kgRequeridos - kgDelMes)} kg"

                val btnCanjear = view.findViewById<Button>(R.id.btnCanjearPromo)
                if (desbloqueada) {
                    btnCanjear.visibility = View.VISIBLE
                    btnCanjear.setOnClickListener {
                        startActivity(Intent(this@RecompensasActivity, CanjeActivity::class.java).apply {
                            putExtra(CanjeActivity.EXTRA_TITULO,      promo.titulo)
                            putExtra(CanjeActivity.EXTRA_SOCIO,       promo.socio)
                            putExtra(CanjeActivity.EXTRA_DESCRIPCION, promo.descripcion)
                            putExtra(CanjeActivity.EXTRA_CODIGO,      promo.codigoCanje)
                            putExtra(CanjeActivity.EXTRA_INSTAGRAM,   promo.instagram)
                            putExtra(CanjeActivity.EXTRA_WEB,         promo.web)
                        })
                    }
                    view.alpha = 1f
                } else {
                    btnCanjear.visibility = View.GONE
                    view.alpha = 0.5f
                }

                return view
            }
        }
    }
}