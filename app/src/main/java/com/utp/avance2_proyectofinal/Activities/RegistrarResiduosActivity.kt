package com.utp.avance2_proyectofinal.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.utp.avance2_proyectofinal.R
import com.utp.avance2_proyectofinal.viewmodel.RegistrarResiduosVM
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import android.app.DatePickerDialog
import androidx.appcompat.app.AlertDialog
import androidx.activity.result.PickVisualMediaRequest

class RegistrarResiduosActivity: BaseActivity(){

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
    private lateinit var btnVovler: Button

    // NUEVO: ImageView de la categoría y su Uri de foto
    private lateinit var imagenCategoria: ImageView
    private var fotoUri: Uri? = null
    private lateinit var etFecha: EditText
    private var fechaSeleccionada: Calendar = Calendar.getInstance()
    private val categorias = listOf("Plástico", "Vidrio", "Papel", "Metal", "Electrónico", "Orgánico")
    private val unidades    = listOf("kg", "g", "lb", "unidades")
    private val formatoVisible = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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

    // NUEVO: launcher para tomar la foto
    private val tomarFotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) {
            mostrarFoto(fotoUri)
        } else {
            Toast.makeText(this, "No se pudo tomar la foto", Toast.LENGTH_SHORT).show()
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
        imagenCategoria = findViewById(R.id.ImagenCategoria)
        etFecha      = findViewById(R.id.etFecha)


        spCategoria.adapter = ArrayAdapter(this,
            R.layout.spinner_item, categorias).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spUnidad.adapter = ArrayAdapter(this,
            R.layout.spinner_item, unidades).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        btAgregar.setOnClickListener { validarYGuardar() }

        btHistorial.setOnClickListener {
            historialLauncher.launch(Intent(this, HistorialResiduosActivity::class.java))
        }

        btnVovler.setOnClickListener {
            finish()
        }

        // NUEVO: click en el ImageView abre la cámara
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

    // NUEVO: crea el archivo ANTES de lanzar el launcher
    private fun abrirCamara() {
        val uri = crearArchivoFoto() // esto ya devuelve Uri no-nulo
        fotoUri = uri
        tomarFotoLauncher.launch(uri) // pasamos el Uri no-nulo directamente
    }

    private fun crearArchivoFoto(): Uri {
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val archivo = File(
            dir,
            "foto_${System.currentTimeMillis()}.jpg"
        )
        // FileProvider convierte file:// → content://
        return FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            archivo
        )
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

    private val elegirDeGaleriaLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            fotoUri = uri
            mostrarFoto(uri)
        } else {
            Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
        }
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
            origen    = origen,
            fecha     = fechaSeleccionada.time   // ← nuevo
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
                    limpiarFormulario()
                }
            }
        }
    }
}