package com.utp.avance2_proyectofinal.Activities

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.utp.avance2_proyectofinal.R
import com.utp.avance2_proyectofinal.data.RegistrarResiduos
import com.utp.avance2_proyectofinal.viewmodel.HistorialResiduosVM
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetalleResiduoActivity : AppCompatActivity() {   // hija → AppCompatActivity

    companion object {
        const val EXTRA_ID        = "extra_id"
        const val EXTRA_CATEGORIA = "extra_categoria"
        const val EXTRA_CANTIDAD  = "extra_cantidad"
        const val EXTRA_UNIDAD    = "extra_unidad"
        const val EXTRA_ORIGEN    = "extra_origen"
        const val EXTRA_FECHA     = "extra_fecha"
        const val EXTRA_FOTO      = "extra_foto"
    }

    private val viewModel: HistorialResiduosVM by viewModels()

    private val categorias = arrayOf("Plástico", "Vidrio", "Papel", "Metal", "Electrónico", "Orgánico")
    private val unidades   = arrayOf("kg", "g", "lb", "unidades")

    private val formatoBD      = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val formatoVisible = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val calFecha = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detalle_residuo)

        // Leer el registro que llegó del historial
        val id        = intent.getLongExtra(EXTRA_ID, -1L)
        val categoria = intent.getStringExtra(EXTRA_CATEGORIA) ?: ""
        val cantidad  = intent.getDoubleExtra(EXTRA_CANTIDAD, 0.0)
        val unidad    = intent.getStringExtra(EXTRA_UNIDAD) ?: ""
        val origen    = intent.getStringExtra(EXTRA_ORIGEN) ?: ""
        val fecha     = intent.getStringExtra(EXTRA_FECHA) ?: ""
        val fotoUri   = intent.getStringExtra(EXTRA_FOTO)

        if (id == -1L) { finish(); return }   // llegó sin datos, no hay qué mostrar

        runCatching { formatoBD.parse(fecha) }.getOrNull()?.let { calFecha.time = it }

        val ivFoto      = findViewById<ImageView>(R.id.ivFotoResiduo)
        val tvSinFoto   = findViewById<TextView>(R.id.tvSinFoto)
        val spCategoria = findViewById<Spinner>(R.id.spCategoriaDet)
        val spUnidad    = findViewById<Spinner>(R.id.spUnidadDet)
        val etCantidad  = findViewById<EditText>(R.id.etCantidadDet)
        val etOrigen    = findViewById<EditText>(R.id.etOrigenDet)
        val etFecha     = findViewById<EditText>(R.id.etFechaDet)

        // Foto (o placeholder si no hay / no se puede leer)
        if (fotoUri != null) {
            runCatching { ivFoto.setImageURI(Uri.parse(fotoUri)) }
            if (ivFoto.drawable == null) tvSinFoto.visibility = View.VISIBLE
        } else {
            tvSinFoto.visibility = View.VISIBLE
        }

        // Precargar campos (spinners SIN placeholder: aquí siempre hay valor)
        spCategoria.adapter = adapterSimple(categorias)
        spCategoria.setSelection(categorias.indexOf(categoria).takeIf { it >= 0 } ?: 0)
        spUnidad.adapter = adapterSimple(unidades)
        spUnidad.setSelection(unidades.indexOf(unidad).takeIf { it >= 0 } ?: 0)
        etCantidad.setText(cantidad.toString())
        etOrigen.setText(origen)
        etFecha.setText(formatoVisible.format(calFecha.time))

        etFecha.setOnClickListener {
            DatePickerDialog(this,
                { _, año, mes, dia ->
                    calFecha.set(año, mes, dia)
                    etFecha.setText(formatoVisible.format(calFecha.time))
                },
                calFecha.get(Calendar.YEAR), calFecha.get(Calendar.MONTH),
                calFecha.get(Calendar.DAY_OF_MONTH)
            ).apply { datePicker.maxDate = System.currentTimeMillis() }.show()
        }

        findViewById<Button>(R.id.btGuardarDet).setOnClickListener {
            val nuevaCantidad = etCantidad.text.toString().toDoubleOrNull()
            val nuevoOrigen   = etOrigen.text.toString().trim()
            if (nuevaCantidad == null) { etCantidad.error = "Cantidad inválida"; return@setOnClickListener }
            if (nuevoOrigen.isEmpty()) { etOrigen.error = "Ingrese el origen"; return@setOnClickListener }

            viewModel.editarResiduo(RegistrarResiduos(
                id        = id,
                categoria = spCategoria.selectedItem.toString(),
                cantidad  = nuevaCantidad,
                unidad    = spUnidad.selectedItem.toString(),
                origen    = nuevoOrigen,
                fecha     = formatoBD.format(calFecha.time),
                fotoUri   = fotoUri            // la foto no cambia aquí, se conserva
            ))
            Toast.makeText(this, "Residuo actualizado", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<Button>(R.id.btEliminarDet).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar registro")
                .setMessage("¿Eliminar $categoria — $cantidad $unidad? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewModel.eliminarResiduo(id)
                    Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
        findViewById<Button>(R.id.btVolverDet).setOnClickListener { finish() }
    }

    private fun adapterSimple(items: Array<String>) =
        ArrayAdapter(this, R.layout.spinner_item, items).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
}