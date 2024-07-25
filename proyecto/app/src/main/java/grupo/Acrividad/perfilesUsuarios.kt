package grupo.Acrividad

import Modelo.ClaseConexion
import Modelo.DataClassPacientes
import RecyclerViewHelpers.Adaptador
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class perfilesUsuarios : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfiles_usuarios)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnAgregar = findViewById<ImageView>(R.id.btnAgregar)
        btnAgregar.setOnClickListener {
            val pantallaInicio = Intent(this, Formulario::class.java)
            startActivity(pantallaInicio)
            overridePendingTransition(0, 0)
        }

        val rcvPacientes = findViewById<RecyclerView>(R.id.rcvPacientes)

        rcvPacientes.layoutManager = LinearLayoutManager(this)

        fun obtenerPacientes(): List<DataClassPacientes> {
            val listaP = mutableListOf<DataClassPacientes>()
            val objConexion = ClaseConexion().cadenaConexion()
            objConexion?.use { conn ->
                val statement = conn.createStatement()
                val resultSet = statement?.executeQuery("SELECT * FROM Pacientes")!!
                while (resultSet.next()) {
                    val id_paciente = resultSet.getInt("id_paciente")
                    val nombres = resultSet.getString("nombres")
                    val apellidos = resultSet.getString("apellidos")
                    val edad = resultSet.getInt("edad")
                    val enfermedad = resultSet.getString("enfermedad")
                    val num_habitacion = resultSet.getInt("num_habitacion")
                    val num_cama = resultSet.getInt("num_cama")
                    val medicamento = resultSet.getString("medicamento")
                    val fecha_ingreso = resultSet.getDate("fecha_ingreso")
                    val hora_aplicacion = resultSet.getTime("hora_aplicacion")
                    val valoresJuntos = DataClassPacientes(
                        id_paciente,
                        nombres,
                        apellidos,
                        edad,
                        enfermedad,
                        num_habitacion,
                        num_cama,
                        medicamento,
                        hora_aplicacion.toString(),
                        fecha_ingreso.toString()
                    )

                    listaP.add(valoresJuntos)
                }
            }

            return listaP

        }


        CoroutineScope(Dispatchers.IO).launch {
            val newsPacientes = obtenerPacientes()
            withContext(Dispatchers.IO){
                (rcvPacientes.adapter as? Adaptador)?.updateLista(newsPacientes)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val pacientesBD = obtenerPacientes()
            withContext(Dispatchers.Main){
                val adapter = Adaptador(pacientesBD)
                rcvPacientes.adapter = adapter
            }
        }
    }
}