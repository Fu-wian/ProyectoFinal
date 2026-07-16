package com.utp.avance2_proyectofinal.Activities

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.utp.avance2_proyectofinal.R
import com.utp.avance2_proyectofinal.viewmodel.RegistrarResiduosVM
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistrarResiduosActivity : BaseActivity() {

    override fun obtenerItemMenu() = R.id.nav_registrar

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

    private lateinit var imagenCategoria: ImageView
    private var fotoUri: Uri? = null
    private lateinit var etFecha: EditText
    private var fechaSeleccionada: Calendar = Calendar.getInstance()

    private val categorias = listOf("Seleccione categoría", "Plástico", "Vidrio", "Papel", "Metal", "Electrónico", "Orgánico")
    private val unidades   = listOf("Seleccione unidad", "kg", "g", "lb", "unidades")
    private val formatoVisible = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private val historialLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            limpiarFormulario()
            Toast.makeText(this, "Volviste del historial", Toast.LENGTH_SHORT).show()
        }
    }

    private val tomarFotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) {
            mostrarFoto(fotoUri)
        } else {
            Toast.makeText(this, "No se pudo tomar la foto", Toast.LENGTH_SHORT).show()
        }
    }

    private val elegirDeGaleriaLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            fotoUri = copiarAGaleriaPropia(uri) ?: uri
            mostrarFoto(uri)
        } else {
            Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrar_residuos)

        spCategoria     = findViewById(R.id.spCategoria)
        spUnidad        = findViewById(R.id.spUnidad)
        etCantidad      = findViewById(R.id.etCantidad)
        etOrigen        = findViewById(R.id.etOrigen)
        cbConfirmar     = findViewById(R.id.cbConfirmar)
        btAgregar       = findViewById(R.id.btAgregar)
        btHistorial     = findViewById(R.id.btHistorial)
        imagenCategoria = findViewById(R.id.ImagenCategoria)
        etFecha         = findViewById(R.id.etFecha)

        spCategoria.adapter = adapterConPlaceholder(categorias)
        spUnidad.adapter    = adapterConPlaceholder(unidades)

        btAgregar.setOnClickListener { validarYGuardar() }

        btHistorial.setOnClickListener {
            historialLauncher.launch(Intent(this, HistorialResiduosActivity::class.java))
        }

        imagenCategoria.setOnClickListener {
            mostrarOpcionesImagen()
        }

        etFecha.setText(formatoVisible.format(fechaSeleccionada.time))  // default: hoy
        etFecha.setOnClickListener {
            DatePickerDialog(
                this,
                { _, año, mes, dia ->
                    fechaSeleccionada.set(año, mes, dia)
                    etFecha.setText(formatoVisible.format(fechaSeleccionada.time))
                },
                fechaSeleccionada.get(Calendar.YEAR),
                fechaSeleccionada.get(Calendar.MONTH),
                fechaSeleccionada.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = System.currentTimeMillis()  // bloquea fechas futuras
            }.show()
        }

        observarViewModel()
    }

    private fun adapterConPlaceholder(items: List<String>) =
        object : ArrayAdapter<String>(this, R.layout.spinner_item, items) {
            override fun isEnabled(position: Int) = position != 0

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent) as TextView
                v.setTextColor(if (position == 0) Color.parseColor("#9AA79C")
                else Color.parseColor("#1B1F1C"))
                return v
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(position, convertView, parent) as TextView
                v.setTextColor(if (position == 0) Color.parseColor("#C5CEC7")
                else Color.parseColor("#1B1F1C"))
                return v
            }
        }.also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

    private fun abrirCamara() {
        val uri = crearArchivoFoto()
        fotoUri = uri
        tomarFotoLauncher.launch(uri)
    }

    private fun crearArchivoFoto(): Uri {
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val archivo = File(dir, "foto_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(this, "${packageName}.fileprovider", archivo)
    }

    private fun mostrarFoto(uri: Uri?) {
        uri ?: return
        imagenCategoria.setImageURI(uri)
    }

    private fun mostrarOpcionesImagen() {
        val opciones = arrayOf("Tomar foto", "Elegir de galería")
        AlertDialog.Builder(this)
            .setTitle("Agregar imagen")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> abrirCamara()
                    1 -> elegirDeGaleriaLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            }
            .show()
    }

    private fun validarYGuardar() {
        val cantidadStr = etCantidad.text.toString().trim()
        val origen      = etOrigen.text.toString().trim()

        if (spCategoria.selectedItemPosition == 0) {
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show(); return
        }
        if (spUnidad.selectedItemPosition == 0) {
            Toast.makeText(this, "Seleccione una unidad", Toast.LENGTH_SHORT).show(); return
        }
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
            origen    = origen,
            fecha     = fechaSeleccionada.time,
            fotoUri   = fotoUri?.toString()
        )
    }

    private fun limpiarFormulario() {
        etCantidad.text.clear()
        etOrigen.text.clear()
        cbConfirmar.isChecked = false
        spCategoria.setSelection(0)
        spUnidad.setSelection(0)
        etCantidad.requestFocus()
        fechaSeleccionada = Calendar.getInstance()
        etFecha.setText(formatoVisible.format(fechaSeleccionada.time))
    }

    private fun observarViewModel() {
        lifecycleScope.launch {
            viewModel.guardado.collect { guardado ->
                if (guardado) {
                    Toast.makeText(this@RegistrarResiduosActivity,
                        "Residuo registrado", Toast.LENGTH_SHORT).show()
                    viewModel.resetGuardado()
                    startActivity(Intent(this@RegistrarResiduosActivity,
                        HistorialResiduosActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                }
            }
        }
    }
    private fun copiarAGaleriaPropia(uri: Uri): Uri? = runCatching {
        val destino = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "foto_${System.currentTimeMillis()}.jpg")
        contentResolver.openInputStream(uri)?.use { entrada ->
            destino.outputStream().use { salida -> entrada.copyTo(salida) }
        }
        FileProvider.getUriForFile(this, "${packageName}.fileprovider", destino)
    }.getOrNull()
}