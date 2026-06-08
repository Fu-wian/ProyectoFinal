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
        btnVolver.setOnClickListener { finish() }

        configurarListView()
        observarViewModel()
    }

    private fun configurarListView() {
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val item = viewModel.residuos.value[position]

            android.app.AlertDialog.Builder(this)
                .setTitle("¿Qué deseas hacer?")
                .setItems(arrayOf("Editar", "Eliminar")) { _, which ->
                    when (which) {
                        0 -> mostrarDialogoEditar(item)
                        1 -> {
                            android.app.AlertDialog.Builder(this)
                                .setTitle("Eliminar registro")
                                .setMessage("¿Eliminar ${item.categoria} — ${item.cantidad} ${item.unidad}?")
                                .setPositiveButton("Eliminar") { _, _ ->
                                    viewModel.eliminarResiduo(item.id)
                                }
                                .setNegativeButton("Cancelar", null)
                                .show()
                        }
                    }
                }
                .show()
            true
        }
    }

    private fun mostrarDialogoEditar(item: RegistrarResiduos) {
        val categorias = arrayOf("Plástico", "Vidrio", "Papel", "Metal", "Electrónico", "Orgánico")
        val unidades   = arrayOf("kg", "g", "lb", "unidades")

        // Inflamos un layout simple con un EditText para la cantidad y origen
        val view = layoutInflater.inflate(android.R.layout.simple_list_item_2, null)

        val etCantidad = EditText(this).apply {
            setText(item.cantidad.toString())
            inputType = android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Cantidad"
        }
        val etOrigen = EditText(this).apply {
            setText(item.origen)
            hint = "Origen"
        }

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 16, 48, 16)
            addView(EditText(context).apply { isEnabled = false; setText(item.categoria) })
            addView(etCantidad)
            addView(etOrigen)
        }

        android.app.AlertDialog.Builder(this)
            .setTitle("Editar Residuo")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevaCantidad = etCantidad.text.toString().toDoubleOrNull()
                val nuevoOrigen   = etOrigen.text.toString().trim()

                if (nuevaCantidad == null || nuevoOrigen.isEmpty()) {
                    Toast.makeText(this, "Datos inválidos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val residuoEditado = item.copy(
                    cantidad = nuevaCantidad,
                    origen   = nuevoOrigen
                )
                viewModel.editarResiduo(residuoEditado)
                Toast.makeText(this, "Residuo actualizado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
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