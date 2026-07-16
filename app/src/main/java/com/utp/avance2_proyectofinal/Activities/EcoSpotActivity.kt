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

class EcoSpotActivity : BaseActivity(), OnMapReadyCallback {


    override fun obtenerItemMenu() = R.id.nav_ecospots

    private lateinit var googleMap: GoogleMap

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

    private val puntosReciclaje = listOf(
        EcoSpot(
            nombre = "Eco Spot UTP",
            descripcion = "Acepta plástico, papel, cartón y residuos reciclables.",
            latitud = 9.0227,
            longitud = -79.5317
        ),
        EcoSpot(
            nombre = "Eco Spot Multiplaza",
            descripcion = "Punto de reciclaje para plástico, vidrio y cartón.",
            latitud = 9.0109,
            longitud = -79.4936
        ),
        EcoSpot(
            nombre = "Eco Spot Parque Omar",
            descripcion = "Punto de reciclaje comunitario.",
            latitud = 9.0043,
            longitud = -79.5081
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eco_spots)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapEcoSpots) as SupportMapFragment

        mapFragment.getMapAsync(this)
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

        agregarMarcadores()

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
    }

    private fun agregarMarcadores() {
        for (punto in puntosReciclaje) {
            val ubicacion = LatLng(punto.latitud, punto.longitud)

            val marker: Marker? = googleMap.addMarker(
                MarkerOptions()
                    .position(ubicacion)
                    .title(punto.nombre)
                    .snippet(punto.descripcion)
            )

            marker?.tag = punto
        }
    }

    private fun mostrarInformacionEcoSpot(ecoSpot: EcoSpot) {
        AlertDialog.Builder(this)
            .setTitle(ecoSpot.nombre)
            .setMessage(ecoSpot.descripcion)
            .setPositiveButton("Ver ruta") { _, _ ->
                abrirUbicacionEnGoogleMaps(ecoSpot)
            }
            .setNegativeButton("Cerrar", null)
            .show()
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