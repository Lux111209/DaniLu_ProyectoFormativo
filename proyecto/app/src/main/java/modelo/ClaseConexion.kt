package Modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {
    fun cadenaConexion(): Connection? {

        try {
            val url = "jdbc:oracle:thin:@25.57.113.220:1521:xe"
            val usuario = "PROYECTO"
            val contrana = "PROYECTO"

            val conection = DriverManager.getConnection(url, usuario, contrana)

            return conection
        }
        catch (e: Exception){
            println("Este es el error: $e")
            return null
        }
    }

}