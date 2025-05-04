package com.example.androidstudio_dsw9

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import com.google.gson.Gson
import java.io.IOException
import android.content.Intent
import android.widget.ImageButton

class Carrito : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        recyclerView = findViewById(R.id.recyclerCarrito)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Cargar productos desde el servidor
        cargarProductos()
    }

    // Esta función es llamada desde el XML cuando el botón es presionado
    fun abrirCarrito(view: View) {
        val intent = Intent(this, CarritoDetalleActivity::class.java)
        startActivity(intent)
    }

    private fun cargarProductos() {
        progressBar.visibility = View.VISIBLE

        val request = Request.Builder()
            .url("http://10.0.2.2/miapp/consultacarrito.php")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@Carrito, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                val productos = Gson().fromJson(json, Array<Producto>::class.java).toList()

                runOnUiThread {
                    recyclerView.adapter = ProductoAdapter(productos)
                    progressBar.visibility = View.GONE
                }
            }
        })
    }
}
