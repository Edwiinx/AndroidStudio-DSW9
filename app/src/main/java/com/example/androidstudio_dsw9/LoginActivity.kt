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
                LoginTask().execute(username, password)
            } else {
                Toast.makeText(this, "Por favor ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class LoginTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            val username = params[0] ?: ""
            val password = params[1] ?: ""

            val url = URL("http://192.168.50.210/miapp/login.php")
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

                return response.trim()
            } catch (e: Exception) {
                e.printStackTrace()
                return "error|${e.message}"
            } finally {
                urlConnection.disconnect()
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (result != null && result.contains("|")) {
                val parts = result.split("|")
                if (parts.size >= 3 && parts[0].toIntOrNull() != null) {
                    val idUsuario = parts[0].toInt()
                    val tipo = parts[1]
                    val username = parts[2]

                    Toast.makeText(this@LoginActivity, "Login exitoso\nUsuario: $username\nTipo: $tipo", Toast.LENGTH_LONG).show()

                    if (tipo.equals("administrador", ignoreCase = true)) {
                        val intent = Intent(this@LoginActivity, Captura::class.java)
                        intent.putExtra("usuario", username)
                        startActivity(intent)
                        finish()
                    } else if (tipo.equals("comprador", ignoreCase = true)) {
                        val intent = Intent(this@LoginActivity, Carrito::class.java)
                        intent.putExtra("usuario", username)
                        intent.putExtra("id_usuario", idUsuario)
                        startActivity(intent)
                        finish()
                    }
                } else if (result.startsWith("error|")) {
                    val errorMessage = if (parts.size > 1) parts[1] else "Error desconocido"
                    Toast.makeText(this@LoginActivity, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@LoginActivity, "Estructura de respuesta inválida: $result", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this@LoginActivity, "Respuesta inesperada: $result", Toast.LENGTH_LONG).show()
            }
        }
    }
}
