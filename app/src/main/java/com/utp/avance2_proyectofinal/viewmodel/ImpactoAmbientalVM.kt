package com.utp.avance2_proyectofinal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.utp.avance2_proyectofinal.repository.ImpactoAmbientalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class ImpactoSemanal(
    val totalKg: Double = 0.0,
    val co2Kg: Double = 0.0,
    val recicladoKg: Double = 0.0,
    val porDia: List<Double> = List(7) { 0.0 }   // índice 0 = Lunes ... 6 = Domingo
)

class ImpactoAmbientalVM(application: Application) : AndroidViewModel(application) {

    private val repository = ImpactoAmbientalRepository(application)

    private val _impacto = MutableStateFlow(ImpactoSemanal())
    val impacto: StateFlow<ImpactoSemanal> = _impacto.asStateFlow()

    companion object {
        // kg de CO2 emitido por cada kg de residuo (valores aproximados de referencia)
        private val FACTOR_CO2 = mapOf(
            "Plástico"    to 6.0,
            "Vidrio"      to 0.9,
            "Papel"       to 1.3,
            "Metal"       to 4.0,
            "Electrónico" to 16.0,
            "Orgánico"    to 0.6
        )
        private val RECICLABLES = setOf("Plástico", "Vidrio", "Papel", "Metal")
    }

    init { cargarImpacto() }

    fun cargarImpacto() {
        viewModelScope.launch {
            val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            // Lunes de la semana actual a las 00:00
            val inicioSemana = Calendar.getInstance().apply {
                firstDayOfWeek = Calendar.MONDAY
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            var total = 0.0
            var co2 = 0.0
            var reciclado = 0.0
            val porDia = MutableList(7) { 0.0 }

            repository.obtenerTodos().forEach { r ->
                val fecha = runCatching { formato.parse(r.fecha) }.getOrNull() ?: return@forEach
                val cal = Calendar.getInstance().apply { time = fecha }
                if (cal.before(inicioSemana)) return@forEach

                val kg = aKilogramos(r.cantidad, r.unidad)
                total += kg
                co2 += kg * (FACTOR_CO2[r.categoria] ?: 1.0)
                if (r.categoria in RECICLABLES) reciclado += kg

                // Calendar: DOMINGO=1 ... SÁBADO=7  →  0=Lunes ... 6=Domingo
                val indiceDia = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
                porDia[indiceDia] += kg
            }

            _impacto.value = ImpactoSemanal(total, co2, reciclado, porDia)
        }
    }

    private fun aKilogramos(cantidad: Double, unidad: String): Double = when (unidad) {
        "kg"       -> cantidad
        "g"        -> cantidad * 0.001
        "lb"       -> cantidad * 0.4536
        "unidades" -> cantidad * 0.25   // estimación: 0.25 kg por unidad
        else       -> cantidad
    }
}