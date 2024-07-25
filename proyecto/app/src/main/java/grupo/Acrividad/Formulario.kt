package grupo.Acrividad

import Modelo.ClaseConexion
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class Formulario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_formulario)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnHome = findViewById<ImageView>(R.id.btnPerfilPaciente)
        btnHome.setOnClickListener {
            val pantallaPerfiles = Intent(this, perfilesUsuarios::class.java)
            startActivity(pantallaPerfiles)
            overridePendingTransition(0, 0)
        }

        val txtNombrePacientes = findViewById<EditText>(R.id.txtNombrePaciente)
        val txtApellidosPacientes = findViewById<EditText>(R.id.txtApellidosPacientes)
        val txtEdadPacientes = findViewById<EditText>(R.id.txtEdadPaciente)
        val txtEnfermedadPacientes = findViewById<EditText>(R.id.txtEnfermedadPaciente)
        val txtNumHabitacion = findViewById<EditText>(R.id.txtNumHabitacion)
        val txtNumCama = findViewById<EditText>(R.id.txtNumCama)
        val txtMedicamentos = findViewById<EditText>(R.id.txtMedicamentos)
        val txtFechaIngreso = findViewById<EditText>(R.id.txtFechaIngreso)
        val txtHoraAplicacion = findViewById<EditText>(R.id.txtHoraAplicacion)

        txtFechaIngreso.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { view, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                    val calendarioSeleccionado = Calendar.getInstance()
                    calendarioSeleccionado.set(anioSeleccionado, mesSeleccionado, diaSeleccionado)
                    val fechaSeleccionada = "$diaSeleccionado/${mesSeleccionado + 1}/$anioSeleccionado"
                    txtFechaIngreso.setText(fechaSeleccionada)
                },
                anio, mes, dia
            )

            datePickerDialog.show()
        }

        val btnAgregarPacientes = findViewById<Button>(R.id.btnAgregarPacientes)

        btnAgregarPacientes.setOnClickListener {

            val nombre = txtNombrePacientes.text.toString()
            val apellido = txtApellidosPacientes.text.toString()
            val edad = txtEdadPacientes.text.toString()
            val enfermedad = txtEnfermedadPacientes.text.toString()
            val habitacion = txtNumHabitacion.text.toString()
            val cama = txtNumCama.text.toString()
            val medicamentos = txtMedicamentos.text.toString()
            val ingreso = txtFechaIngreso.text.toString()
            val hora = txtHoraAplicacion.text.toString()

            if(nombre.isEmpty() || apellido.isEmpty() || edad.isEmpty() || enfermedad.isEmpty() || habitacion.isEmpty() || cama.isEmpty() || medicamentos.isEmpty() || ingreso.isEmpty() || hora.isEmpty()){
                Toast.makeText(
                    this,
                    "Complete todos los campos antes de continuar",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                CoroutineScope(Dispatchers.IO).launch {
                    val objConexion = ClaseConexion().cadenaConexion()
                    val addPaciente =
                        objConexion?.prepareStatement("INSERT INTO Pacientes(nombres, apellidos, edad, enfermedad, num_habitacion, num_cama, medicamentos, fecha_ingreso, hora_aplicacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")!!
                    addPaciente.setString(1, txtNombrePacientes.text.toString())
                    addPaciente.setString(2, txtApellidosPacientes.text.toString())
                    addPaciente.setInt(3, txtEdadPacientes.text.toString().toInt())
                    addPaciente.setString(4, txtEnfermedadPacientes.text.toString())
                    addPaciente.setInt(5, txtNumHabitacion.text.toString().toInt())
                    addPaciente.setInt(6, txtNumCama.text.toString().toInt())
                    addPaciente.setString(7, txtMedicamentos.text.toString())
                    addPaciente.setString(8, txtFechaIngreso.text.toString())
                    addPaciente.setString(9, txtHoraAplicacion.text.toString())
                    addPaciente.executeQuery()

                    withContext(Dispatchers.Main) {
                        AlertDialog.Builder(this@Formulario)
                            .setTitle("El Registro se ha hecho exitosamente!")
                        txtNombrePacientes.setText("")
                        txtApellidosPacientes.setText("")
                        txtEdadPacientes.setText("")
                        txtEnfermedadPacientes.setText("")
                        txtNumHabitacion.setText("")
                        txtNumCama.setText("")
                        txtMedicamentos.setText("")
                        txtFechaIngreso.setText("")
                        txtHoraAplicacion.setText("")
                    }
                }
            }
        }
    }
}