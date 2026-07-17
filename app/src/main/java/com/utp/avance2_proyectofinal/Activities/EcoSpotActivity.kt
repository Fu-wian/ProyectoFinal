package com.utp.avance2_proyectofinal.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.utp.avance2_proyectofinal.R
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import com.utp.avance2_proyectofinal.data.EcoSpot
import androidx.lifecycle.ViewModelProvider
import com.utp.avance2_proyectofinal.viewmodel.EcoSpotVM
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.google.android.gms.maps.model.BitmapDescriptorFactory


class EcoSpotActivity : BaseActivity(), OnMapReadyCallback {


    override fun obtenerItemMenu() = R.id.nav_ecospots

    private lateinit var googleMap: GoogleMap

    private lateinit var ecoSpotVM: EcoSpotVM
    private val marcadoresEcoSpots = mutableListOf<Marker>()

    private val solicitarPermisoUbicacion =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permisoConcedido ->
            if (permisoConcedido) {
                activarUbicacionUsuario()
            } else {
                Toast.makeText(
                    this,
                    "Permiso de ubicación denegado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eco_spots)

        ecoSpotVM = ViewModelProvider(this)[EcoSpotVM::class.java]
        observarEcoSpots()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapEcoSpots) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun observarEcoSpots() {
        ecoSpotVM.listaEcoSpots.observe(this) { lista ->
            if (::googleMap.isInitialized) {
                agregarMarcadores(lista)
            }
        }
    }

    private fun activarUbicacionUsuario() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
        } else {
            solicitarPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onMapReady(mapa: GoogleMap) {
        googleMap = mapa

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        activarUbicacionUsuario()

        val ubicacionInicial = LatLng(9.0227, -79.5317)
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(ubicacionInicial, 13f)
        )

        googleMap.setOnMarkerClickListener { marker ->
            val ecoSpot = marker.tag as? EcoSpot

            if (ecoSpot != null) {
                mostrarInformacionEcoSpot(ecoSpot)
            }

            true
        }

        googleMap.setOnMapLongClickListener { latLng ->
            mostrarDialogoAgregarEcoSpot(latLng)
        }

        ecoSpotVM.cargarEcoSpots()
    }

    private fun agregarMarcadores(listaEcoSpots: List<EcoSpot>) {
        for (marker in marcadoresEcoSpots) {
            marker.remove()
        }

        marcadoresEcoSpots.clear()

        for (punto in listaEcoSpots) {
            val ubicacion = LatLng(punto.latitud, punto.longitud)

            val colorMarcador = if (punto.esPrecargado) {
                BitmapDescriptorFactory.HUE_GREEN
            } else {
                BitmapDescriptorFactory.HUE_AZURE
            }

            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(ubicacion)
                    .title(punto.nombre)
                    .snippet("${punto.descripcion}\nTipo: ${punto.tipoResiduo}")
                    .icon(BitmapDescriptorFactory.defaultMarker(colorMarcador))
            )

            marker?.tag = punto

            if (marker != null) {
                marcadoresEcoSpots.add(marker)
            }
        }
    }

    private fun mostrarInformacionEcoSpot(ecoSpot: EcoSpot) {
        val mensaje = """
        ${ecoSpot.descripcion}
        
        Tipo de residuo: ${ecoSpot.tipoResiduo}
    """.trimIndent()

        val builder = AlertDialog.Builder(this)
            .setTitle(ecoSpot.nombre)
            .setMessage(mensaje)
            .setPositiveButton("Ver ruta") { _, _ ->
                abrirUbicacionEnGoogleMaps(ecoSpot)
            }
            .setNegativeButton("Cerrar", null)

        if (!ecoSpot.esPrecargado) {
            builder.setNeutralButton("Eliminar", null)
        }

        val dialogo = builder.create()

        dialogo.setOnShowListener {
            if (!ecoSpot.esPrecargado) {
                val btnEliminar = dialogo.getButton(AlertDialog.BUTTON_NEUTRAL)

                btnEliminar.setOnClickListener {
                    dialogo.dismiss()
                    confirmarEliminarEcoSpot(ecoSpot)
                }
            }
        }

        dialogo.show()
    }

    private fun confirmarEliminarEcoSpot(ecoSpot: EcoSpot) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar punto")
            .setMessage("¿Deseas eliminar el punto '${ecoSpot.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                val eliminado = ecoSpotVM.eliminarEcoSpot(ecoSpot)

                if (eliminado) {
                    Toast.makeText(
                        this,
                        "Punto eliminado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "No se pudo eliminar el punto",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoAgregarEcoSpot(latLng: LatLng) {
        val vista = layoutInflater.inflate(R.layout.agregar_ecospot, null)

        val etNombre = vista.findViewById<EditText>(R.id.etNombreEcoSpot)
        val etDescripcion = vista.findViewById<EditText>(R.id.etDescripcionEcoSpot)
        val spinnerTipoResiduo = vista.findViewById<Spinner>(R.id.spinnerTipoResiduo)

        val tiposResiduo = listOf(
            "Plástico",
            "Papel",
            "Cartón",
            "Vidrio",
            "Metal",
            "Electrónico",
            "General"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            tiposResiduo
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoResiduo.adapter = adapter

        val dialogo = AlertDialog.Builder(this)
            .setTitle("Agregar punto de reciclaje")
            .setView(vista)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialogo.setOnShowListener {
            val btnGuardar = dialogo.getButton(AlertDialog.BUTTON_POSITIVE)

            btnGuardar.setOnClickListener {
                val nombre = etNombre.text.toString().trim()
                val descripcion = etDescripcion.text.toString().trim()
                val tipoResiduo = spinnerTipoResiduo.selectedItem.toString()

                val guardado = ecoSpotVM.agregarEcoSpot(
                    nombre = nombre,
                    descripcion = descripcion,
                    tipoResiduo = tipoResiduo,
                    latitud = latLng.latitude,
                    longitud = latLng.longitude
                )

                if (guardado) {
                    Toast.makeText(
                        this,
                        "Punto de reciclaje guardado",
                        Toast.LENGTH_SHORT
                    ).show()

                    dialogo.dismiss()
                } else {
                    Toast.makeText(
                        this,
                        "Completa todos los campos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        dialogo.show()
    }

    private fun abrirUbicacionEnGoogleMaps(ecoSpot: EcoSpot) {
        val uri = Uri.parse("google.navigation:q=${ecoSpot.latitud},${ecoSpot.longitud}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            val webUri = Uri.parse(
                "https://www.google.com/maps/search/?api=1&query=${ecoSpot.latitud},${ecoSpot.longitud}"
            )
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            startActivity(webIntent)
        }
    }
}