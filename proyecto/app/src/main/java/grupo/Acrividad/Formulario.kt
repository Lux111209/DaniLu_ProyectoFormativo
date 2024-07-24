package grupo.Acrividad

import Modelo.ClaseConexion
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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
        val txtNombre = findViewById<EditText>(R.id.txtNombre)
        val txtApellido = findViewById<EditText>(R.id.txtApellido)
        val txtEdad = findViewById<EditText>(R.id.txtEdad)
        val txtEnfermedad = findViewById<EditText>(R.id.txtEnfermedad)
        val txtHabitacion = findViewById<EditText>(R.id.txtHabitacion)
        val txtCama = findViewById<EditText>(R.id.txtCama)
        val txtMedicamento = findViewById<EditText>(R.id.txtMedicamentos)
        val txtAplicacion = findViewById<EditText>(R.id.txtHora)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            val nombre = txtNombre.text.toString()
            val apellido = txtApellido.text.toString()
            val edad = txtEdad.text.toString()
            val enfermedad = txtEnfermedad.text.toString()
            val habitacion = txtHabitacion.text.toString()
            val cama = txtCama.text.toString()
            val medicamentos = txtMedicamento.text.toString()
            val aplicacion = txtAplicacion.text.toString()

            if (nombre.isEmpty() || apellido.isEmpty() || edad.isEmpty() || enfermedad.isEmpty() || habitacion.isEmpty() || cama.isEmpty() || medicamentos.isEmpty() || aplicacion.isEmpty()) {
                Toast.makeText(this, "Ingresa datos que sean válidos", Toast.LENGTH_SHORT).show()
            } else {
                Log.i(
                    "Test de Credenciales",
                    "Nombre: $nombre, Apellido: $apellido, Edad: $edad, Enfermedad: $enfermedad, Habitación: $habitacion, Cama: $cama, Medicamentos: $medicamentos y Aplicación: $aplicacion"
                )
            }
        }

        btnRegistrar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val objConexion = ClaseConexion().cadenaConexion()
                val addPaciente = objConexion?.prepareStatement("insert into Pacientes (UUID_Paciente, nombre, apellido, edad, numero_habitacion, numero_cama, medicamentos, hora_aplicacion) values (?, ?, ?, ?, ?, ?, ?, ?)")!!

                addPaciente.setString(1, UUID.randomUUID().toString())
                addPaciente.setString(2, txtNombre.text.toString())
                addPaciente.setString(3, txtApellido.text.toString())
                addPaciente.setString(4, txtEdad.text.toString())
                addPaciente.setString(5, txtHabitacion.text.toString())
                addPaciente.setString(6, txtCama.text.toString())
                addPaciente.setString(7, txtMedicamento.text.toString())
                addPaciente.setString(8, txtAplicacion.text.toString())

                addPaciente.executeUpdate()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Formulario, "Se ha creado un nuevo paciente", Toast.LENGTH_SHORT).show()
                    txtNombre.setText("")
                    txtApellido.setText("")
                    txtEdad.setText("")
                    txtEnfermedad.setText("")
                    txtHabitacion.setText("")
                    txtCama.setText("")
                    txtMedicamento.setText("")
                    txtAplicacion.setText("")
                }
            }
        }
    }
}