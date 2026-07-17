package com.utp.avance2_proyectofinal.Activities

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.utp.avance2_proyectofinal.R
import com.utp.avance2_proyectofinal.data.Campania

class CampaniasActivity : AppCompatActivity() {

    private lateinit var listCampanias: ListView

    // Campañas de ejemplo
    private val campanias = listOf(
        Campania("Jornada de limpieza de playa",
            "Recolección de plásticos y voluntariado ambiental en Cinta Costera",
            "Sábado 25 de julio, 8:00 AM", 0.0, 0.0),
        Campania("Recolección de electrónicos",
            "Trae tus dispositivos viejos para reciclaje seguro en la UTP",
            "Viernes 31 de julio, 9:00 AM", 0.0, 0.0),
        Campania("Feria del reciclaje comunitario",
            "Talleres, trueque de reciclables y charlas en Parque Omar",
            "Domingo 2 de agosto, 10:00 AM", 0.0, 0.0),
        Campania("Reciclatón de vidrio y papel",
            "Meta comunitaria: 2 toneladas en un día, Multiplaza",
            "Sábado 8 de agosto, 7:00 AM", 0.0, 0.0)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.campanias)

        listCampanias = findViewById(R.id.listCampanias)
        findViewById<Button>(R.id.btnVolverCampanias).setOnClickListener { finish() }

        listCampanias.adapter = object : ArrayAdapter<Campania>(
            this, R.layout.item_campania, campanias
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.item_campania, parent, false)
                val camp = campanias[position]
                view.findViewById<TextView>(R.id.tvNombreCamp).text = camp.nombre
                view.findViewById<TextView>(R.id.tvDescCamp).text = camp.descripcion
                view.findViewById<TextView>(R.id.tvPieCamp).text = "📅 ${camp.fecha}"
                return view
            }
        }
    }
}