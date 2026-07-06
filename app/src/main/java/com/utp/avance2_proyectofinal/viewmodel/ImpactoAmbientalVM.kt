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
    val co2EvitadoKg: Double = 0.0,
    val totalSemanaPasadaKg: Double = 0.0,
    val diasActivos: Int = 0,
    val porDia: List<Double> = List(7) { 0.0 },      // 0 = Lunes ... 6 = Domingo
    val porCategoria: Map<String, Double> = emptyMap(),
    val rangoSemana: String = ""
)

class ImpactoAmbientalVM(application: Application) : AndroidViewModel(application) {

    private val repository = ImpactoAmbientalRepository(application)

    private val _impacto = MutableStateFlow(ImpactoSemanal())
    val impacto: StateFlow<ImpactoSemanal> = _impacto.asStateFlow()

    companion object {
        // kg de CO2 que se EVITAN por cada kg reciclado, en vez de producir
        // material nuevo (valores aproximados de referencia)
        private val CO2_EVITADO = mapOf(
            "Plástico"    to 1.5,
            "Vidrio"      to 0.3,
            "Papel"       to 0.9,
            "Metal"       to 9.0,
            "Electrónico" to 1.2,
            "Orgánico"    to 0.25   // compostaje vs vertedero
        )
    }

    init { cargarImpacto() }

    fun cargarImpacto() {
        viewModelScope.launch {
            val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            val inicioSemana = Calendar.getInstance().apply {
                firstDayOfWeek = Calendar.MONDAY
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val inicioSemanaPasada = (inicioSemana.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, -7)
            }
            val finSemana = (inicioSemana.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, 6)
            }

            var total = 0.0
            var co2Evitado = 0.0
            var totalPasada = 0.0
            val porDia = MutableList(7) { 0.0 }
            val porCategoria = mutableMapOf<String, Double>()

            repository.obtenerTodos().forEach { r ->
                val fecha = runCatching { formato.parse(r.fecha) }.getOrNull() ?: return@forEach
                val cal = Calendar.getInstance().apply { time = fecha }
                val kg = aKilogramos(r.cantidad, r.unidad)

                when {
                    !cal.before(inicioSemana) -> {
                        total += kg
                        co2Evitado += kg * (CO2_EVITADO[r.categoria] ?: 0.5)

                        val indiceDia = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
                        porDia[indiceDia] += kg
                        porCategoria[r.categoria] = (porCategoria[r.categoria] ?: 0.0) + kg
                    }
                    !cal.before(inicioSemanaPasada) -> totalPasada += kg
                }
            }

            val formatoDia = SimpleDateFormat("d 'de' MMM", Locale("es"))
            val rango = "${formatoDia.format(inicioSemana.time)} al ${formatoDia.format(finSemana.time)}"

            _impacto.value = ImpactoSemanal(
                totalKg             = total,
                co2EvitadoKg        = co2Evitado,
                totalSemanaPasadaKg = totalPasada,
                diasActivos         = porDia.count { it > 0 },
                porDia              = porDia,
                porCategoria        = porCategoria,
                rangoSemana         = rango
            )
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