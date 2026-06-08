package com.utp.avance2_proyectofinal.Activities

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.utp.avance2_proyectofinal.data.RegistrarResiduos
import com.utp.avance2_proyectofinal.R
import com.utp.avance2_proyectofinal.viewmodel.HistorialResiduosVM
import kotlinx.coroutines.launch

class HistorialResiduosActivity : AppCompatActivity(){
    private val viewModel: HistorialResiduosVM by viewModels()

    private lateinit var listView: ListView
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.historial_residuos)

        listView  = findViewById(R.id.listView)
        btnVolver = findViewById(R.id.btnVolver)
        btnVolver.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        configurarListView()
        observarViewModel()
    }

    private fun configurarListView() {
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val item = listView.adapter.getItem(position) as RegistrarResiduos
            android.app.AlertDialog.Builder(this)
                .setTitle("Eliminar registro")
                .setMessage("¿Eliminar ${item.categoria} – ${item.cantidad} ${item.unidad}?")
                .setPositiveButton("Eliminar") { _, _ -> viewModel.eliminarResiduo(item.id) }
                .setNegativeButton("Cancelar", null)
                .show()
            true
        }
    }

    private fun observarViewModel() {
        lifecycleScope.launch {
            viewModel.residuos.collect { lista ->
                val textos = lista.map {
                    "${it.categoria} — ${it.cantidad} ${it.unidad}\nOrigen: ${it.origen}  |  ${it.fecha}"
                }
                listView.adapter = ArrayAdapter(
                    this@HistorialResiduosActivity,
                    android.R.layout.simple_list_item_2,
                    android.R.id.text1,
                    textos
                )
            }
        }
    }

}