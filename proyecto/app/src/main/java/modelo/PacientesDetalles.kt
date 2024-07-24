package Modelo

data class PacientesDetalles(
    val nombres: String,
    val apellidos: String,
    val edad: Int,
    val enfermedad: String,
    val num_habitacion: Int,
    val num_cama: Int,
    val medicamento: String,
    val fecha_ingreso: String,
    val hora_aplicacion: String
)
