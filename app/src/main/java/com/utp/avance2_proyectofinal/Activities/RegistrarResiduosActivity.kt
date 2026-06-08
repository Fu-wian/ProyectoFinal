package com.utp.avance2_proyectofinal.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
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

    private lateinit var btnVovler : Button

    private val categorias = listOf("Plástico", "Vidrio", "Papel", "Metal", "Electrónico", "Orgánico")
    private val unidades    = listOf("kg", "g", "lb", "unidades")

    private val historialLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            etCantidad.text.clear()
            etOrigen.text.clear()
            cbConfirmar.isChecked = false
            Toast.makeText(this, "Volviste del historial", Toast.LENGTH_SHORT).show()
        }
    }

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
        btnVovler    = findViewById(R.id.btVolver)

        spCategoria.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, categorias).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spUnidad.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, unidades).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        btAgregar.setOnClickListener { validarYGuardar() }

        btHistorial.setOnClickListener {
            historialLauncher.launch(Intent(this, HistorialResiduosActivity::class.java))
        }

        btnVovler.setOnClickListener {
            finish()
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