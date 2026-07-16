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

class HistorialResiduosActivity : BaseActivity(){

    override fun obtenerItemMenu() = R.id.nav_historial

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

        val spFiltro = findViewById<Spinner>(R.id.spFiltroCategoria)
        val opcionesFiltro = listOf("Todas las categorías", "Plástico", "Vidrio", "Papel", "Metal", "Electrónico", "Orgánico")

        spFiltro.adapter = ArrayAdapter(this, R.layout.spinner_item, opcionesFiltro).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val filtro = if (position == 0) null else opcionesFiltro[position]
                viewModel.cargarResiduos(filtro)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
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

        val etCantidad = EditText(this).apply {
            setText(item.cantidad.toString())
            inputType = android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Cantidad"
        }
        val etOrigen = EditText(this).apply {
            setText(item.origen)
            hint = "Origen"
        }

        val spCategoria = Spinner(this).apply {
            adapter = ArrayAdapter(this@HistorialResiduosActivity,
                android.R.layout.simple_spinner_item, categorias).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            // Selecciona la categoría actual del item
            setSelection(categorias.indexOf(item.categoria).takeIf { it >= 0 } ?: 0)
        }

        val spUnidad = Spinner(this).apply {
            adapter = ArrayAdapter(this@HistorialResiduosActivity,
                android.R.layout.simple_spinner_item, unidades).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            // Selecciona la unidad actual del item
            setSelection(unidades.indexOf(item.unidad).takeIf { it >= 0 } ?: 0)
        }

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 16, 48, 16)
            addView(spCategoria)
            addView(etCantidad)
            addView(spUnidad)
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
                    categoria = spCategoria.selectedItem.toString(),
                    cantidad  = nuevaCantidad,
                    unidad    = spUnidad.selectedItem.toString(),
                    origen    = nuevoOrigen
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
                listView.adapter = object : ArrayAdapter<RegistrarResiduos>(
                    this@HistorialResiduosActivity, R.layout.item_residuo, lista
                ) {
                    override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                        val view = convertView ?: layoutInflater.inflate(R.layout.item_residuo, parent, false)
                        val item = lista[position]
                        view.findViewById<TextView>(R.id.tvTitulo).text = "${item.categoria} — ${item.cantidad} ${item.unidad}"
                        view.findViewById<TextView>(R.id.tvDetalle).text = "Origen: ${item.origen}  |  ${item.fecha}"
                        return view
                    }
                }
                val total = viewModel.totalCount.value
                findViewById<TextView>(R.id.tvContador).text =
                    if (lista.size == total) "$total registros"
                    else "${lista.size} de $total registros"
            }
        }
    }

}