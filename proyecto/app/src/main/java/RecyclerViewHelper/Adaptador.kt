package RecyclerViewHelpers

import Modelo.ClaseConexion
import Modelo.DataClassPacientes
import Modelo.PacientesDetalles
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import grupo.Acrividad.R
import java.sql.ResultSet
import java.sql.SQLException

class Adaptador(var Datos: List<DataClassPacientes>): RecyclerView.Adapter<ViewHolder>() {

    fun updateLista(newLista: List<DataClassPacientes>) {
        Datos = newLista
        notifyDataSetChanged()
    }

    fun updateScreen(
        id_paciente: Int,
        new_edad: Int,
        new_enfermadad: String,
        new_num_habitacion: Int,
        new_num_cama: Int,
        new_fecha_ingreso: String,
    ) {
        val index = Datos.indexOfFirst { it.id_paciente == id_paciente }
        if (index != -1){
            Datos[index].edad = new_edad
            Datos[index].enfermedad = new_enfermadad
            Datos[index].num_habitacion = new_num_habitacion
            Datos[index].num_cama = new_num_cama
            Datos[index].fecha_ingreso = new_fecha_ingreso
            notifyItemChanged(index)
        }
    }

    fun deleteData(context: Context, id_paciente: Int, position: Int) {
        val dataList = Datos.toMutableList()
        dataList.removeAt(position)

        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            if(objConexion != null) {
                try {
                    objConexion.autoCommit = false

                    val borrarPaciente =
                        objConexion.prepareStatement("delete from Pacientes where id_paciente = ?")!!
                    borrarPaciente.setInt(1, id_paciente)
                    val pacienteEliminado = borrarPaciente.executeUpdate()
                    Log.d("deleteData", "El Paciente se eliminado exitosamente: $pacienteEliminado")

                    objConexion.commit()
                    Log.d("deleteData", "Commit exitoso")

                    withContext(Dispatchers.Main) {
                        Datos = dataList.toList()
                        notifyItemRemoved(position)
                        notifyDataSetChanged()
                        Toast.makeText(
                            context,
                            "El Paciente se eliminado exitosamente ",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } catch (e: SQLException) {
                    Log.e("deleteData", "error sql", e)
                    try {
                        objConexion.rollback()
                        Log.d("deleteData", "Todo bien con el rollback")
                    }catch (rollbackEx: SQLException){
                        Log.e("deleteData", "No esta bien el rollback", rollbackEx)
                    }
                    withContext(Dispatchers.Main)
                    {
                        Toast.makeText(context, "Da error al borrar paciente: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception){
                    Log.e("deleteData", "Este error no se esperaba", e)
                    withContext(Dispatchers.Main)
                    {
                        Toast.makeText(context, "Este error no se esperaba: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    try {
                        objConexion.close()
                        Log.d("deleteData", "Se ha cerrado la conexion.")
                    }catch (closeEx: SQLException)
                    {
                        Log.e("deleteData", "Se ha cerrado la conexion", closeEx)
                    }
                }
            }else{
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "no funciona la base de datos",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun updateData(context: Context, newEdad: Int, newEnfermedad: String, newnum_habitacion: Int, newnum_cama: Int,  newfecha_ingreso: String, id_paciente: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            if(objConexion != null) {
                try {
                    val actualizarPaciente =
                        objConexion.prepareStatement("update Pacientes set edad = ?, enfermedad = ?, num_habitacion = ?, num_cama = ?, fecha_ingreso = ?  where id_paciente = ?")!!
                    actualizarPaciente.setInt(1, newEdad)
                    actualizarPaciente.setString(2, newEnfermedad)
                    actualizarPaciente.setInt(3, newnum_habitacion)
                    actualizarPaciente.setInt(4, newnum_cama)
                    actualizarPaciente.setString(5, newfecha_ingreso)
                    actualizarPaciente.setInt(6, id_paciente)
                    actualizarPaciente.executeUpdate()

                    withContext(Dispatchers.Main) {
                        updateScreen(
                            id_paciente,
                            newEdad,
                            newEnfermedad,
                            newnum_habitacion,
                            newnum_cama,
                            newfecha_ingreso
                        )
                        Toast.makeText(
                            context,
                            "Actualizado perfectamente",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Revisar bien", Toast.LENGTH_SHORT)
                            .show()
                    }
                    e.printStackTrace()
                }finally {
                    objConexion.close()
                }
            }else{
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(context, "La ip esta mala ", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)

            .inflate(R.layout.activity_item_card, parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = Datos[position]
        holder.txtPacienteNombre.text = item.nombres
        holder.txtPacienteApellido.text = item.apellidos

        holder.btnBorrar.setOnClickListener {
            val context = holder.itemView.context

            val builder = androidx.appcompat.app.AlertDialog.Builder(context)
            builder.setTitle("Borrar")
            builder.setMessage("Estás seguro que deseas este paciente?")

            builder.setPositiveButton("Si"){dialog, which ->
                deleteData(context, item.id_paciente, position)
            }

            builder.setNeutralButton("No"){dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

        }

        holder.btnEditar.setOnClickListener {
            val context = holder.itemView.context

            val dialogView = LayoutInflater.from(context).inflate(R.layout.activity_editar_perfil, null)
            val editEdad = dialogView.findViewById<EditText>(R.id.txtEdadEdit)
            val editEnfermedad = dialogView.findViewById<EditText>(R.id.txtEnfermedadEdit)
            val editHabitacion = dialogView.findViewById<EditText>(R.id.txtNumHabitacionEdit)
            val editCama = dialogView.findViewById<EditText>(R.id.txtNumCamaEdit)
            val editIngreso = dialogView.findViewById<EditText>(R.id.txtFechaEdit)

            editIngreso.setOnClickListener {
                val calendario = Calendar.getInstance()
                val anio = calendario.get(Calendar.YEAR)
                val mes = calendario.get(Calendar.MONTH)
                val dia = calendario.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    context,
                    { view, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                        val calendarioSeleccionado = Calendar.getInstance()
                        calendarioSeleccionado.set(anioSeleccionado, mesSeleccionado, diaSeleccionado)
                        val fechaSeleccionada = "$diaSeleccionado/${mesSeleccionado + 1}/$anioSeleccionado"
                        editIngreso.setText(fechaSeleccionada)
                    },
                    anio, mes, dia
                )
                datePickerDialog.show()
            }

            editEdad.setHint(item.edad.toString())
            editEnfermedad.setHint(item.enfermedad)
            editHabitacion.setHint(item.num_habitacion.toString())
            editCama.setHint(item.num_cama.toString())
            editIngreso.setHint(item.fecha_ingreso)

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Actualizacion de paciente")
            builder.setView(dialogView)
            builder.setPositiveButton("Guardar"){dialog, _ ->
                val newEdad = editEdad.text.toString().toIntOrNull() ?: item.edad
                val newEnfermedad = editEnfermedad.text.toString()
                val newHabitacion = editHabitacion.text.toString().toIntOrNull() ?: item.num_habitacion
                val newCama = editCama.text.toString().toIntOrNull() ?: item.num_cama
                val newIngreso = editIngreso.text.toString()

                updateData(context, newEdad, newEnfermedad, newHabitacion, newCama, newIngreso, item.id_paciente)


                dialog.dismiss()
            }

            builder.setNegativeButton("Cancelar") {dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        holder.itemView.setOnClickListener {
            mostrarDialog(holder.itemView.context, item.id_paciente)
        }
    }

    private fun mostrarDialog(context: Context, idPaciente: Int) {
        val builder = AlertDialog.Builder(context)
        val dialogLayout =
            LayoutInflater.from(context).inflate(R.layout.activity_vista_paciente, null)
        builder.setView(dialogLayout)

        val alertDialog = builder.create()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val pacienteDetalles = obtenerDetallesPaciente(idPaciente)

                withContext(Dispatchers.Main) {
                    dialogLayout.findViewById<TextView>(R.id.txtPacienteNombreI)?.text =
                        pacienteDetalles.nombres
                    dialogLayout.findViewById<TextView>(R.id.txtPacienteApellidoI)?.text =
                        pacienteDetalles.apellidos
                    dialogLayout.findViewById<TextView>(R.id.txtEdadPacienteI)?.text =
                        pacienteDetalles.edad.toString()
                    dialogLayout.findViewById<TextView>(R.id.txtEnfermedadPacienteI)?.text =
                        pacienteDetalles.enfermedad
                    dialogLayout.findViewById<TextView>(R.id.txtNumeroHabitacionI)?.text =
                        pacienteDetalles.num_habitacion.toString()
                    dialogLayout.findViewById<TextView>(R.id.txtNumeroCamaI)?.text =
                        pacienteDetalles.num_cama.toString()
                    dialogLayout.findViewById<TextView>(R.id.txtFechaIngresoI)?.text =
                        pacienteDetalles.fecha_ingreso
                    dialogLayout.findViewById<TextView>(R.id.txtMedicamentoI)?.text =
                        pacienteDetalles.medicamento
                    dialogLayout.findViewById<TextView>(R.id.txtHoraAplicacionI)?.text =
                        pacienteDetalles.hora_aplicacion

                    alertDialog.show()
                }

            } catch (e: Exception){
                Log.e("AdaptadorPacientes", "El error es en el dialog", e)
                withContext(Dispatchers.Main){
                    Toast.makeText(context, "Agregar todos los campos", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun obtenerDetallesPaciente(idPaciente: Int): PacientesDetalles{
        val objConexion = ClaseConexion().cadenaConexion()
        if(objConexion == null){
            throw IllegalStateException("La conexion es nula")
        }

        val statement = objConexion.createStatement()
        val query = """
            SELECT 
                p.nombres AS Nombres,
                p.apellidos AS Apellidos,
                p.edad AS Edad,
                p.enfermedad AS Enfermedad,
                p.num_habitacion AS Numero_Habitacion,
                p.num_cama AS Numero_Cama,
                p.fecha_ingreso AS Ingreso,
                p.medicamentos AS Medicamentos,
                p.hora_aplicacion as Hora_Aplicacion
            INNER JOIN 
                Pacientes p ON am.id_paciente = p.id_paciente
            WHERE p.id_paciente = $idPaciente
        """.trimIndent()

        val resultSet: ResultSet = statement.executeQuery(query)
        if(!resultSet.next()){
            throw IllegalStateException("No hay ningun detalle de paciente")
        }

        val pacienteDetalles = PacientesDetalles(
            nombres = resultSet.getString("Nombres"),
            apellidos = resultSet.getString("Apellidos"),
            edad = resultSet.getInt("Edad"),
            enfermedad = resultSet.getString("Enfermedad"),
            num_habitacion = resultSet.getInt("Numero_Habitacion"),
            num_cama = resultSet.getInt("Numero_Cama"),
            fecha_ingreso = resultSet.getString("Ingreso"),
            medicamento = resultSet.getString("Medicamentos"),
            hora_aplicacion = resultSet.getString("hora_aplicaciones")
        )

        resultSet.close()
        statement.close()
        objConexion.close()

        return pacienteDetalles
    }


}