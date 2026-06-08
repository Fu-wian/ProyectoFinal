package com.utp.avance2_proyectofinal.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.utp.avance2_proyectofinal.R
import com.utp.avance2_proyectofinal.viewmodel.RegistrarResiduosVM
import kotlinx.coroutines.launch

class RegistrarResiduosActivity: AppCompatActivity(){
    companion object {
        const val RESULT_RESIDUO_AGREGADO = "extra_registrar"
    }

    private val viewModel: RegistrarResiduosVM by viewModels()

    private lateinit var spCategoria: Spinner
    private lateinit var spUnidad: Spinner
    private lateinit var etCantidad: EditText
    private lateinit var etOrigen: EditText
    private lateinit var cbConfirmar: CheckBox
    private lateinit var btAgregar: Button
    private lateinit var btHistorial: Button

    private val categorias = listOf("Plástico", "Vidrio", "Papel", "Metal", "Electrónico", "Orgánico")
    private val unidades    = listOf("kg", "g", "lb", "unidades")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrar_residuos)

        spCategoria  = findViewById(R.id.spCategoria)
        spUnidad     = findViewById(R.id.spUnidad)
        etCantidad   = findViewById(R.id.etCantidad)
        etOrigen     = findViewById(R.id.etOrigen)
        cbConfirmar  = findViewById(R.id.cbConfirmar)
        btAgregar    = findViewById(R.id.btAgregar)
        btHistorial  = findViewById(R.id.btHistorial)

        spCategoria.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, categorias).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spUnidad.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, unidades).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        btAgregar.setOnClickListener { validarYGuardar() }

        btHistorial.setOnClickListener {
            startActivity(Intent(this, HistorialResiduosActivity::class.java))
        }

        observarViewModel()
    }

    private fun validarYGuardar() {
        val cantidadStr = etCantidad.text.toString().trim()
        val origen      = etOrigen.text.toString().trim()

        if (cantidadStr.isEmpty()) { etCantidad.error = "Ingrese una cantidad"; return }
        if (origen.isEmpty())      { etOrigen.error   = "Ingrese el origen";    return }
        if (!cbConfirmar.isChecked) {
            Toast.makeText(this, "Confirme que los datos son correctos", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.agregarResiduo(
            categoria = spCategoria.selectedItem.toString(),
            cantidad  = cantidadStr.toDouble(),
            unidad    = spUnidad.selectedItem.toString(),
            origen    = origen
        )
    }

    private fun observarViewModel() {
        lifecycleScope.launch {
            viewModel.guardado.collect { guardado ->
                if (guardado) {
                    Toast.makeText(this@RegistrarResiduosActivity,
                        "Residuo registrado", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK, Intent().putExtra(RESULT_RESIDUO_AGREGADO, true))
                    viewModel.resetGuardado()
                    finish()
                }
            }
        }
    }
}