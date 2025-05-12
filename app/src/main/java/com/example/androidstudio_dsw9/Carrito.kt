package com.example.androidstudio_dsw9

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class Carrito : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var listaCompleta: List<Producto>
    private var idUsuario: Int = -1  // ID dinámico

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        recyclerView = findViewById(R.id.recyclerCarrito)
        progressBar = findViewById(R.id.progressBar)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Obtener ID del usuario
        idUsuario = intent.getIntExtra("id_usuario", -1)

        // Botón para cerrar sesión
        val cerrarSesionBtn = findViewById<Button>(R.id.cerrar_sesion)
        cerrarSesionBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // se usa para filtrar los productos por categoria
        findViewById<ImageButton>(R.id.btnPC).setOnClickListener { filtrarPorCategoria("PC") }
        findViewById<ImageButton>(R.id.btnAltavoz).setOnClickListener { filtrarPorCategoria("SPE") }
        findViewById<ImageButton>(R.id.btnTeclado).setOnClickListener { filtrarPorCategoria("KEY") }
        findViewById<ImageButton>(R.id.btnComponente).setOnClickListener { filtrarPorCategoria("COM") }
        findViewById<ImageButton>(R.id.btnMonitor).setOnClickListener { filtrarPorCategoria("MON") }
        findViewById<ImageButton>(R.id.btnMouse).setOnClickListener { filtrarPorCategoria("MOU") }
        findViewById<ImageButton>(R.id.btnLaptop).setOnClickListener { filtrarPorCategoria("LAP") }
        findViewById<ImageButton>(R.id.btnCaja).setOnClickListener { filtrarPorCategoria("CAS") }
        findViewById<ImageButton>(R.id.btnTodos).setOnClickListener { mostrarTodos() }

        // Cargar todos los productos del carrito
        cargarProductos(idUsuario)
    }

    fun abrirCarrito(view: View) {
        Log.d("Carrito", "abrirCarrito() invoked")
        try {
            val intent = Intent(this, CarritoDetalleActivity::class.java)
            intent.putExtra("id_usuario", idUsuario)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("Carrito", "Error al lanzar CarritoDetalleActivity", e)
            Toast.makeText(this, "No puedo abrir el carrito: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun cargarProductos(idUsuario: Int) {
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
                    listaCompleta = productos
                    recyclerView.adapter = ProductoAdapter(listaCompleta, idUsuario)
                    progressBar.visibility = View.GONE
                }
            }
        })
    }

    private fun filtrarPorCategoria(idCategoria: String) {
        val filtrados = listaCompleta.filter { it.ID_CATEGORIA == idCategoria }
        recyclerView.adapter = ProductoAdapter(filtrados, idUsuario)
    }

    private fun mostrarTodos() {
        recyclerView.adapter = ProductoAdapter(listaCompleta, idUsuario)
    }
}
