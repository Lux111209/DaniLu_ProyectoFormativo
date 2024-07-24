package Modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {
    fun cadenaConexion(): Connection? {

        try {
            val url = "jdbc:oracle:thin:@192.168.1.16:1521:xe"
            val usuario = "SYSTEM"
            val contrana = "191012"

            val conection = DriverManager.getConnection(url, usuario, contrana)

            return conection
        }
        catch (e: Exception){
            println("Este es el error: $e")
            return null
        }
    }

}