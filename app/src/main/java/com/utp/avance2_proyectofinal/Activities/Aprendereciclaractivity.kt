package com.utp.avance2_proyectofinal.Activities

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.utp.avance2_proyectofinal.R

class AprendeReciclarActivity : AppCompatActivity() {

    // Modelo simple para cada pregunta del quiz
    data class Pregunta(
        val texto: String,
        val opciones: List<String>,
        val indiceCorrecta: Int,
        val explicacion: String
    )

    private val preguntas = listOf(
        Pregunta(
            texto = "¿Cuál de estos materiales tarda más en degradarse?",
            opciones = listOf("Papel", "Vidrio", "Cáscara de banana"),
            indiceCorrecta = 1,
            explicacion = "El vidrio puede tardar más de 4,000 años en degradarse, ¡por eso reciclarlo es tan importante!"
        ),
        Pregunta(
            texto = "¿Qué residuo NO debe ir con el reciclaje común?",
            opciones = listOf("Botella plástica limpia", "Caja de pizza con grasa", "Lata de aluminio"),
            indiceCorrecta = 1,
            explicacion = "El cartón contaminado con grasa o comida no se puede reciclar; contamina el resto del lote."
        ),
        Pregunta(
            texto = "Reciclar 1 kg de metal evita aproximadamente...",
            opciones = listOf("9 kg de CO₂", "0.5 kg de CO₂", "No evita CO₂"),
            indiceCorrecta = 0,
            explicacion = "Producir metal nuevo consume muchísima energía; reciclarlo evita cerca de 9 kg de CO₂ por kilo."
        ),
        Pregunta(
            texto = "¿Qué debes hacer con una botella antes de reciclarla?",
            opciones = listOf("Dejarla con líquido", "Enjuagarla y aplastarla", "Quemarla"),
            indiceCorrecta = 1,
            explicacion = "Enjuagarla evita contaminar otros materiales y aplastarla ahorra espacio en el transporte."
        ),
        Pregunta(
            texto = "¿Cuál es el mejor destino para los residuos orgánicos?",
            opciones = listOf("El vertedero", "El compostaje", "El contenedor de vidrio"),
            indiceCorrecta = 1,
            explicacion = "El compostaje convierte los residuos orgánicos en abono y evita emisiones de metano en los vertederos."
        )
    )

    private var indiceActual = 0
    private var aciertos = 0
    private var respondida = false

    private lateinit var tvProgreso: TextView
    private lateinit var tvPregunta: TextView
    private lateinit var rgOpciones: RadioGroup
    private lateinit var opciones: List<RadioButton>
    private lateinit var tvRetroalimentacion: TextView
    private lateinit var btnResponder: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aprende_reciclar)

        tvProgreso          = findViewById(R.id.tvProgresoQuiz)
        tvPregunta          = findViewById(R.id.tvPregunta)
        rgOpciones          = findViewById(R.id.rgOpciones)
        tvRetroalimentacion = findViewById(R.id.tvRetroalimentacion)
        btnResponder        = findViewById(R.id.btnResponder)

        opciones = listOf(
            findViewById(R.id.rbOpcion1),
            findViewById(R.id.rbOpcion2),
            findViewById(R.id.rbOpcion3)
        )

        findViewById<Button>(R.id.btnVolverAprende).setOnClickListener { finish() }

        btnResponder.setOnClickListener {
            if (!respondida) responder() else siguiente()
        }

        mostrarPregunta()
    }

    private fun mostrarPregunta() {
        val p = preguntas[indiceActual]

        tvProgreso.text = "Pregunta ${indiceActual + 1} de ${preguntas.size}"
        tvPregunta.text = p.texto

        opciones.forEachIndexed { i, rb ->
            rb.text = p.opciones[i]
            rb.isEnabled = true
        }

        rgOpciones.clearCheck()
        tvRetroalimentacion.visibility = View.GONE
        btnResponder.text = "Responder"
        respondida = false
    }

    private fun responder() {
        val seleccionada = opciones.indexOfFirst { it.isChecked }

        if (seleccionada == -1) {
            Toast.makeText(this, "Selecciona una opción", Toast.LENGTH_SHORT).show()
            return
        }

        val p = preguntas[indiceActual]
        val correcta = seleccionada == p.indiceCorrecta
        if (correcta) aciertos++

        tvRetroalimentacion.visibility = View.VISIBLE
        tvRetroalimentacion.text =
            if (correcta) "✅ ¡Correcto! ${p.explicacion}"
            else "❌ Incorrecto. ${p.explicacion}"
        tvRetroalimentacion.setTextColor(
            if (correcta) Color.parseColor("#2E7D32") else Color.parseColor("#C62828")
        )

        opciones.forEach { it.isEnabled = false }

        btnResponder.text =
            if (indiceActual < preguntas.size - 1) "Siguiente" else "Ver resultado"
        respondida = true
    }

    private fun siguiente() {
        if (indiceActual < preguntas.size - 1) {
            indiceActual++
            mostrarPregunta()
        } else {
            mostrarResultado()
        }
    }

    private fun mostrarResultado() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Resultado del quiz")
            .setMessage("Acertaste $aciertos de ${preguntas.size} preguntas.\n\n" +
                    if (aciertos == preguntas.size) "¡Excelente! Eres un experto del reciclaje 🌎"
                    else "¡Sigue aprendiendo, cada respuesta cuenta!")
            .setPositiveButton("Reintentar") { _, _ ->
                indiceActual = 0
                aciertos = 0
                mostrarPregunta()
            }
            .setNegativeButton("Salir") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }
}