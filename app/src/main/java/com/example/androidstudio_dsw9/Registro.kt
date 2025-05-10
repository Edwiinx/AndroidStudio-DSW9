package com.example.androidstudio_dsw9

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import android.content.Intent

class Registro : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val registerBtn = findViewById<Button>(R.id.registerbtn)
        val userInput = findViewById<EditText>(R.id.userregister)
        val passwordInput = findViewById<EditText>(R.id.inputpassword)

        registerBtn.setOnClickListener {
            val username = userInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val tipo = "ADMINISTRADOR" // O "usuario", según necesites

            if (username.isNotEmpty() && password.isNotEmpty()) {
                RegistroTask().execute(username, password, tipo)
            } else {
                Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class RegistroTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            val username = params[0] ?: ""
            val password = params[1] ?: ""
            val tipo = params[2] ?: ""

            try {
                val url = URL("http://172.29.66.12/miApp/registro.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val postData = "user=${URLEncoder.encode(username, "UTF-8")}" +
                        "&password=${URLEncoder.encode(password, "UTF-8")}" +
                        "&type=${URLEncoder.encode(tipo, "UTF-8")}"

                val output = BufferedWriter(OutputStreamWriter(connection.outputStream, "UTF-8"))
                output.write(postData)
                output.flush()
                output.close()

                val inputStream = BufferedReader(InputStreamReader(connection.inputStream))
                val result = inputStream.readText()
                inputStream.close()

                return result

            } catch (e: Exception) {
                return "error|${e.message}"
            }
        }

        override fun onPostExecute(result: String?) {
            try {
                val json = JSONObject(result)
                if (json.has("mensaje_mostrar") && json.getString("mensaje_mostrar") == "exito") {
                    Toast.makeText(this@Registro, "Registro exitoso", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@Registro, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this@Registro, "Usuario ya existe", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@Registro, "Error al registrar: $result", Toast.LENGTH_LONG).show()
            }
        }
    }
}
