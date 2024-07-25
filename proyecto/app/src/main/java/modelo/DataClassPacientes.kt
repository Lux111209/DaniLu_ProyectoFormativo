package Modelo

data class DataClassPacientes(
    var id_paciente: Int,
    var nombres: String,
    var apellidos: String,
    var edad: Int,
    var enfermedad: String,
    var num_habitacion: Int,
    var num_cama: Int,
    var medicamento: String,
    var fecha_ingreso: String,
    var hora_aplicacion: String
)