package com.utp.avance2_proyectofinal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.utp.avance2_proyectofinal.data.Promo
import com.utp.avance2_proyectofinal.repository.ImpactoAmbientalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RecompensasVM(application: Application) : AndroidViewModel(application) {

    private val repository = ImpactoAmbientalRepository(application)

    private val _kgDelMes = MutableStateFlow(0.0)
    val kgDelMes: StateFlow<Double> = _kgDelMes.asStateFlow()

    val promos = listOf(
        Promo(
            titulo       = "10% de descuento",
            socio        = "Café Verde",
            descripcion  = "Válido en cualquier bebida caliente. Presenta el QR al cajero.",
            kgRequeridos = 2.0,
            codigoCanje  = "TINAECO-CV-10",
            instagram    = "https://instagram.com/cafeverde",
            web          = "https://cafeverde.com"
        ),
        Promo(
            titulo       = "Bebida gratis",
            socio        = "EcoMarket",
            descripcion  = "Elige cualquier bebida natural del menú. Un uso por mes.",
            kgRequeridos = 5.0,
            codigoCanje  = "TINAECO-EM-BEB",
            instagram    = "https://instagram.com/ecomarket",
            web          = "https://ecomarket.com"
        ),
        Promo(
            titulo       = "Cupón de B/.5",
            socio        = "Super Reciclo",
            descripcion  = "Descuento directo en tu próxima compra de productos reciclados.",
            kgRequeridos = 10.0,
            codigoCanje  = "TINAECO-SR-5",
            instagram    = "https://instagram.com/superreciclo",
            web          = "https://superreciclo.com"
        ),
        Promo(
            titulo       = "Entrada 2x1 al cine",
            socio        = "CineEco",
            descripcion  = "Dos entradas al precio de una, cualquier función de lunes a jueves.",
            kgRequeridos = 20.0,
            codigoCanje  = "TINAECO-CE-2X1",
            instagram    = "https://instagram.com/cineeco",
            web          = "https://cineeco.com"
        )
    )

    init { cargarKgDelMes() }

    fun cargarKgDelMes() {
        viewModelScope.launch {
            val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val ahora   = Calendar.getInstance()
            var total   = 0.0
            repository.obtenerTodos().forEach { r ->
                val fecha = runCatching { formato.parse(r.fecha) }.getOrNull() ?: return@forEach
                val cal   = Calendar.getInstance().apply { time = fecha }
                if (cal.get(Calendar.MONTH) == ahora.get(Calendar.MONTH) &&
                    cal.get(Calendar.YEAR)  == ahora.get(Calendar.YEAR)) {
                    total += aKg(r.cantidad, r.unidad)
                }
            }
            _kgDelMes.value = total
        }
    }

    private fun aKg(cantidad: Double, unidad: String) = when (unidad) {
        "kg" -> cantidad; "g" -> cantidad * 0.001
        "lb" -> cantidad * 0.4536; else -> cantidad * 0.25
    }
}