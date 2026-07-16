package com.utp.avance2_proyectofinal.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.utp.avance2_proyectofinal.data.Campania
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.material.card.MaterialCardView
import com.utp.avance2_proyectofinal.R
import java.util.Locale



class CampaniasActivity : AppCompatActivity() {

    private lateinit var layoutCampanias: LinearLayout

    // Campañas de ejemplo (en producción vendrían de un servidor)
    private val campanias = listOf(
        Campania("Jornada de limpieza de playa", "Recolección de plásticos y voluntariado ambiental",
            "Sábado 25 de julio, 8:00 AM", 8.9936, -79.5197),                 // Cinta Costera
        Campania("Recolección de electrónicos", "Trae tus dispositivos viejos para reciclaje seguro",
            "Viernes 31 de julio, 9:00 AM", 9.0227, -79.5317),                // UTP
        Campania("Feria del reciclaje comunitario", "Talleres, trueque de reciclables y charlas",
            "Domingo 2 de agosto, 10:00 AM", 9.0043, -79.5081),               // Parque Omar
        Campania("Reciclatón de vidrio y papel", "Meta comunitaria: 2 toneladas en un día",
            "Sábado 8 de agosto, 7:00 AM", 9.0109, -79.4936)                  // Multiplaza
    )

    private val solicitarPermiso = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) cargarConUbicacion() else mostrarCampanias(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.campanias)

        layoutCampanias = findViewById(R.id.layoutCampanias)
        findViewById<Button>(R.id.btnVolverCampanias).setOnClickListener { finish() }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            cargarConUbicacion()
        } else {
            solicitarPermiso.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")   // se llama solo con permiso verificado
    private fun cargarConUbicacion() {
        LocationServices.getFusedLocationProviderClient(this)
            .lastLocation
            .addOnSuccessListener { ubicacion -> mostrarCampanias(ubicacion) }
            .addOnFailureListener { mostrarCampanias(null) }
    }

    private fun mostrarCampanias(ubicacion: Location?) {
        layoutCampanias.removeAllViews()

        if (ubicacion == null) {
            findViewById<TextView>(R.id.tvSubtituloCampanias).text =
                "Actividades disponibles (activa la ubicación para ver distancias)"
        }

        // Con ubicación: ordenar por cercanía. Sin ella: orden original.
        val lista = if (ubicacion != null) {
            campanias.sortedBy { distanciaKm(ubicacion, it) }
        } else campanias

        val densidad = resources.displayMetrics.density

        lista.forEach { camp ->
            val card = MaterialCardView(this).apply {
                radius = 16 * densidad
                cardElevation = 2 * densidad
                setCardBackgroundColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = (10 * densidad).toInt() }
            }

            val contenido = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                val pad = (16 * densidad).toInt()
                setPadding(pad, pad, pad, pad)
            }

            contenido.addView(TextView(this).apply {
                text = camp.nombre
                textSize = 15f
                setTextColor(Color.parseColor("#1B5E20"))
                setTypeface(typeface, Typeface.BOLD)
            })

            contenido.addView(TextView(this).apply {
                text = camp.descripcion
                textSize = 13f
                setTextColor(Color.parseColor("#37474F"))
            })

            val pie = "📅 ${camp.fecha}" + if (ubicacion != null)
                "   📍 a ${String.format(Locale.getDefault(), "%.1f", distanciaKm(ubicacion, camp))} km"
            else ""

            contenido.addView(TextView(this).apply {
                text = pie
                textSize = 12f
                setTextColor(Color.parseColor("#66796B"))
            })

            card.addView(contenido)
            layoutCampanias.addView(card)
        }
    }

    private fun distanciaKm(ubicacion: Location, camp: Campania): Double {
        val resultado = FloatArray(1)
        Location.distanceBetween(
            ubicacion.latitude, ubicacion.longitude,
            camp.latitud, camp.longitud, resultado
        )
        return resultado[0] / 1000.0
    }
}