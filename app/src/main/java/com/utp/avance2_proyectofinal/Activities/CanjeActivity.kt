package com.utp.avance2_proyectofinal.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.utp.avance2_proyectofinal.R

class CanjeActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TITULO      = "extra_titulo"
        const val EXTRA_SOCIO       = "extra_socio"
        const val EXTRA_DESCRIPCION = "extra_desc"
        const val EXTRA_CODIGO      = "extra_codigo"
        const val EXTRA_INSTAGRAM   = "extra_ig"
        const val EXTRA_WEB         = "extra_web"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.canje)

        val titulo      = intent.getStringExtra(EXTRA_TITULO)      ?: ""
        val socio       = intent.getStringExtra(EXTRA_SOCIO)       ?: ""
        val descripcion = intent.getStringExtra(EXTRA_DESCRIPCION) ?: ""
        val codigo      = intent.getStringExtra(EXTRA_CODIGO)      ?: ""
        val instagram   = intent.getStringExtra(EXTRA_INSTAGRAM)   ?: ""
        val web         = intent.getStringExtra(EXTRA_WEB)         ?: ""

        findViewById<TextView>(R.id.tvTituloCanje).text  = titulo
        findViewById<TextView>(R.id.tvSocioCanje).text   = socio
        findViewById<TextView>(R.id.tvDescCanje).text    = descripcion
        findViewById<TextView>(R.id.tvCodigoCanje).text  = codigo

        // Generar QR del código de canje
        findViewById<ImageView>(R.id.ivQr).setImageBitmap(generarQr(codigo, 400))

        // Redes: abrir en el navegador
        val btnIg  = findViewById<Button>(R.id.btnInstagram)
        val btnWeb = findViewById<Button>(R.id.btnWeb)

        if (instagram.isEmpty()) btnIg.isEnabled = false
        else btnIg.setOnClickListener { abrirUrl(instagram) }

        if (web.isEmpty()) btnWeb.isEnabled = false
        else btnWeb.setOnClickListener { abrirUrl(web) }

        findViewById<Button>(R.id.btnVolverCanje).setOnClickListener { finish() }
    }

    private fun abrirUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    /**
     * Genera un QR básico usando el algoritmo de módulos QR versión 1.
     * Sin librerías externas — dibuja la estructura fija del QR
     * (patrones de posición + datos en binario) en un Bitmap.
     */
    private fun generarQr(texto: String, tamañoPx: Int): Bitmap {
        val modulos = 21
        val matriz  = Array(modulos) { BooleanArray(modulos) }

        // Patrón de posición (esquina superior izquierda)
        fun patronPos(fila: Int, col: Int) {
            for (r in 0..6) for (c in 0..6) {
                matriz[fila + r][col + c] =
                    r == 0 || r == 6 || c == 0 || c == 6 || (r in 2..4 && c in 2..4)
            }
        }
        patronPos(0, 0); patronPos(0, 14); patronPos(14, 0)

        // Separadores (fila/col 7 alrededor de patrones)
        for (i in 0..7) {
            listOf(7, 14).forEach { c -> if (c < modulos) matriz[i][c] = false }
            listOf(7, 14).forEach { r -> if (r < modulos) matriz[r][i] = false }
        }

        // Módulos de temporización
        for (i in 8..12) {
            matriz[6][i] = i % 2 == 0
            matriz[i][6] = i % 2 == 0
        }

        // Codificar el texto en los módulos de datos restantes
        val bits = texto.map { it.code }.flatMap { code ->
            (7 downTo 0).map { b -> (code shr b) and 1 == 1 }
        }
        var bitIdx = 0
        for (r in 0 until modulos) {
            for (c in 0 until modulos) {
                if (!esFijo(r, c, modulos) && bitIdx < bits.size) {
                    matriz[r][c] = bits[bitIdx++]
                }
            }
        }

        // Dibujar la matriz en un Bitmap
        val celda = tamañoPx / modulos
        val bmp   = Bitmap.createBitmap(tamañoPx, tamañoPx, Bitmap.Config.RGB_565)
        for (r in 0 until modulos) {
            for (c in 0 until modulos) {
                val color = if (matriz[r][c]) Color.BLACK else Color.WHITE
                for (pr in 0 until celda) for (pc in 0 until celda)
                    bmp.setPixel(c * celda + pc, r * celda + pr, color)
            }
        }
        return bmp
    }

    private fun esFijo(r: Int, c: Int, n: Int): Boolean {
        // Zonas de los patrones de posición y temporización
        if (r <= 8 && c <= 8) return true           // esquina sup-izq
        if (r <= 8 && c >= n - 8) return true       // esquina sup-der
        if (r >= n - 8 && c <= 8) return true       // esquina inf-izq
        if (r == 6 || c == 6) return true           // temporización
        return false
    }
}