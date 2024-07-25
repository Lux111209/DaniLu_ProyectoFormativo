package grupo.Acrividad

import Modelo.ClaseConexion
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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

class MainActivity: AppCompatActivity() {

    companion object variablesGlobales{
        lateinit var miMorreo: String
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtCorreo = findViewById<EditText>(R.id.txtCorreoInicio)
        val txtContrasena = findViewById<EditText>(R.id.txtContrasenaInicio)
        val btnLogin = findViewById<Button>(R.id.btnIniciarSesion)
        val correoPattern = Regex ("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")

        btnLogin.setOnClickListener {

            val correo = txtCorreo.text.toString()
            val contrasena = txtContrasena.text.toString()

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(
                    this,
                    "Error, debes de llenar todas las casillas",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!correoPattern.matches(correo)) {
                Toast.makeText(
                    this,
                    "El correo electronico debe de ser valido.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val pantallaListadoPacientes = Intent(this, perfilesUsuarios::class.java)

                CoroutineScope(Dispatchers.IO).launch {

                    val objConexion = ClaseConexion().cadenaConexion()

                    val comprobarUsuario =
                        objConexion?.prepareStatement("SELECT * FROM Usuarios WHERE correo = ? AND contrasena = ?")!!
                    comprobarUsuario.setString(1, txtCorreo.text.toString())
                    comprobarUsuario.setString(2, txtContrasena.text.toString())
                    val resultado = comprobarUsuario.executeQuery()

                    if (resultado.next()) {
                        val correoIng = txtCorreo.text.toString()
                        pantallaListadoPacientes.putExtra("correoIng", correoIng)

                        miMorreo = txtCorreo.text.toString()
                        runOnUiThread{
                            Toast.makeText(this@MainActivity, "Bienvenid@ a nuestra aplicacion!", Toast.LENGTH_SHORT).show()
                        }
                        startActivity(pantallaListadoPacientes)
                        finish()
                    } else {
                        runOnUiThread{
                            Toast.makeText(this@MainActivity, "Usuario no se ha encontrado, porfavor verifica las credenciales.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(this@MainActivity, "Has salido de la aplicación", Toast.LENGTH_SHORT).show()
        finishAffinity()
    }
}