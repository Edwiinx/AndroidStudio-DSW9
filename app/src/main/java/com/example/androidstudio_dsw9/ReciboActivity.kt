package com.example.androidstudio_dsw9

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ReciboActivity : AppCompatActivity() {

    private lateinit var tvFecha: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var tvNumeroVisa: TextView
    private var idUsuario: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_recibo)

        // Referencias a los TextView del layout
        tvFecha = findViewById(R.id.tvFecha)
        tvTotal = findViewById(R.id.tvTotal)
        tvDescripcion = findViewById(R.id.tvDescripcion)
        tvNumeroVisa = findViewById(R.id.tvNumeroVisa)

        idUsuario = intent.getIntExtra("id_usuario", 0)

        obtenerRecibo()
        val btnVolverCatalogo: Button = findViewById(R.id.btnVolverCatalogo)
        btnVolverCatalogo.setOnClickListener {
            val intent = Intent(this, Carrito::class.java) // Asegúrate que sea tu clase de catálogo
            intent.putExtra("id_usuario", idUsuario)
            startActivity(intent)
            finish()
        }

    }

    private fun obtenerRecibo() {
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("id_usuario", idUsuario.toString())
            .build()

        val request = Request.Builder()
            .url("http://192.168.0.4/miapp/obtener_recibo.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ReciboActivity", "Error de conexión: ${e.message}")
            }


            override fun onResponse(call: Call, response: Response) {
                val respuesta = response.body?.string()
                Log.d("ReciboActivity", "Respuesta del servidor: $respuesta")
                runOnUiThread {
                    try {
                        val json = JSONObject(respuesta)

                        if (json.has("error")) {
                            tvDescripcion.text = "Error: ${json.getString("error")}"
                            return@runOnUiThread
                        }

                        val descripcion = json.getString("descripcion")
                        val total = json.getDouble("total")
                        val fecha = json.getString("fecha")
                        val tarjeta = json.getString("tarjeta")

                        val ultimos4 = if (tarjeta.length >= 4) tarjeta.takeLast(4) else tarjeta

                        tvFecha.text = "Fecha: $fecha"
                        tvTotal.text = "Total: $total"
                        tvDescripcion.text = "Descripción: $descripcion"
                        tvNumeroVisa.text = "Tarjeta: ****-****-****-$ultimos4"

                    } catch (e: Exception) {
                        Log.e("ReciboActivity", "Error al parsear JSON: ${e.message}")
                        tvDescripcion.text = "Error al procesar datos"
                    }
                }
            }
        })

    }

}

