package com.example.androidstudio_dsw9

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.loginbtn)
        val usernameEditText = findViewById<EditText>(R.id.inputuser)
        val passwordEditText = findViewById<EditText>(R.id.inputpassword)
        val toRegister = findViewById<TextView>(R.id.logintoregister)

        toRegister.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                Log.d("LoginDebug", "Username: $username")
                Log.d("LoginDebug", "Password: $password")

                LoginTask().execute(username, password)
            } else {
                Toast.makeText(this@LoginActivity, "Por favor ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class LoginTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String {
            val username = params[0] ?: ""
            val password = params[1] ?: ""

            val url = URL("http://172.29.66.12/miApp/login.php")
            val urlConnection = url.openConnection() as HttpURLConnection

            try {
                val postData = "username=${URLEncoder.encode(username, "UTF-8")}&password=${URLEncoder.encode(password, "UTF-8")}"

                urlConnection.requestMethod = "POST"
                urlConnection.doOutput = true
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                urlConnection.setRequestProperty("Content-Length", postData.length.toString())

                val outputStream = urlConnection.outputStream
                outputStream.write(postData.toByteArray(Charsets.UTF_8))
                outputStream.flush()
                outputStream.close()

                val inputStream = urlConnection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }
                inputStream.close()

                return response
            } catch (e: Exception) {
                e.printStackTrace()
                return "Error: ${e.message}"
            } finally {
                urlConnection.disconnect()
            }
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (result?.startsWith("success") == true) {
                val parts = result.split("|")
                if (parts.size >= 3) {
                    val tipo = parts[1]  // Tipo de usuario
                    val username = parts[2]  // Nombre de usuario

                    Toast.makeText(this@LoginActivity, "Login exitoso\nUsuario: $username\nTipo: $tipo", Toast.LENGTH_LONG).show()

                    if (tipo.equals("administrador", ignoreCase = true)) {
                        val intent = Intent(this@LoginActivity, Captura::class.java)
                        intent.putExtra("usuario", username)
                        startActivity(intent)
                        finish() // Para cerrar LoginActivity y no volver con el botón atrás
                    }

                } else {
                    Toast.makeText(this@LoginActivity, "Error en la estructura de respuesta: $result", Toast.LENGTH_LONG).show()
                }
            } else if (result?.startsWith("error") == true) {
                val parts = result.split("|")
                val errorMessage = if (parts.size > 1) parts[1] else "Error desconocido. La respuesta del servidor no contiene detalles."
                Toast.makeText(this@LoginActivity, "Error: $errorMessage", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@LoginActivity, "Respuesta completamente inesperada. Respuesta: $result", Toast.LENGTH_LONG).show()
            }
        }


    }
}
