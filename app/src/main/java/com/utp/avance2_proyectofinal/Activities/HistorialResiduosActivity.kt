package com.utp.avance2_proyectofinal.Activities

import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.utp.avance2_proyectofinal.data.RegistrarResiduos
import com.utp.avance2_proyectofinal.R
import com.utp.avance2_proyectofinal.viewmodel.HistorialResiduosVM
import kotlinx.coroutines.launch

class HistorialResiduosActivity : BaseActivity(){

    override fun obtenerItemMenu() = R.id.nav_historial

    private val viewModel: HistorialResiduosVM by viewModels()

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.historial_residuos)

        listView  = findViewById(R.id.listView)


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
    override fun onResume() {
        super.onResume()
        viewModel.cargarResiduos()
    }
    private fun configurarListView() {
        listView.setOnItemClickListener { _, _, position, _ ->
            val item = viewModel.residuos.value[position]
            startActivity(Intent(this, DetalleResiduoActivity::class.java).apply {
                putExtra(DetalleResiduoActivity.EXTRA_ID, item.id)
                putExtra(DetalleResiduoActivity.EXTRA_CATEGORIA, item.categoria)
                putExtra(DetalleResiduoActivity.EXTRA_CANTIDAD, item.cantidad)
                putExtra(DetalleResiduoActivity.EXTRA_UNIDAD, item.unidad)
                putExtra(DetalleResiduoActivity.EXTRA_ORIGEN, item.origen)
                putExtra(DetalleResiduoActivity.EXTRA_FECHA, item.fecha)
                putExtra(DetalleResiduoActivity.EXTRA_FOTO, item.fotoUri)
            })
        }
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