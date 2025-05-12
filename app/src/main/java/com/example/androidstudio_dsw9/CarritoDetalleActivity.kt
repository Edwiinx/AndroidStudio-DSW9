package com.example.androidstudio_dsw9

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class CarritoDetalleActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CarritoAdapter
    private lateinit var textTotal: TextView
    private lateinit var btnComprar: Button
    private val carritoList = mutableListOf<Producto>()
    private var idUsuario: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito_detalle)

        recyclerView = findViewById(R.id.recyclerViewCarrito)
        textTotal = findViewById(R.id.textTotal)
        btnComprar = findViewById(R.id.btnComprar)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Obtener el ID del usuario
        idUsuario = intent.getIntExtra("id_usuario", 0)
        if (idUsuario == 0) {
            Toast.makeText(this, "Error: id_usuario no válido", Toast.LENGTH_SHORT).show()
            return
        }


        adapter = CarritoAdapter(
            carritoList,
            onCantidadCambiada = { calcularTotal() },
            idUsuario = idUsuario,
            onProductoEliminado = { cargarCarrito() }
        )
        recyclerView.adapter = adapter

        cargarCarrito()

        // Acción para el botón de compra
        btnComprar.setOnClickListener {
            val intent = Intent(this, ProcesoPagoActivity::class.java)
            intent.putExtra("id_usuario", idUsuario)
            intent.putExtra("total", carritoList.sumOf { it.PRECIO_UNITARIO.toDouble() * it.CANTIDAD })
            startActivity(intent)
        }

    }

    private fun cargarCarrito() {
        val url = "http://192.168.0.4/miapp/cargarcarrito.php?ID_USUARIO=$idUsuario" // HOLA
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CarritoDetalleActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let {
                        val json = it.string()
                        try {
                            Log.d("RESPUESTA_JSON", json)

                            val productos = Gson().fromJson(json, Array<Producto>::class.java)
                            runOnUiThread {
                                // 1. Limpiar lista actual
                                carritoList.clear()

                                // 2. Agregar nuevos productos
                                carritoList.addAll(productos)

                                // 3. Notificar  que los datos cambiaron
                                adapter.notifyDataSetChanged()

                                // 4. Calcular total actualizado
                                calcularTotal()
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                Toast.makeText(this@CarritoDetalleActivity, "Error al procesar los datos", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        })
    }

    //Funcion para calcular el total

    private fun calcularTotal() {
        val total = carritoList.sumOf { it.PRECIO_UNITARIO.toDouble() * it.CANTIDAD }
        textTotal.text = "Total: $${"%.2f".format(total)}"
    }
}
