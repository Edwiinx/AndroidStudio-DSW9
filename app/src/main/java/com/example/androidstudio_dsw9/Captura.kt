package com.example.androidstudio_dsw9

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class Captura : AppCompatActivity() {

    private var selectedImageName: String? = null
    private lateinit var selectedImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.captura)

        val nombre = findViewById<EditText>(R.id.nombreedittext)
        val precio = findViewById<EditText>(R.id.precioedittext)
        val descripcion = findViewById<EditText>(R.id.descripcionedittext)
        val numeroSerie = findViewById<EditText>(R.id.nserieedittext)
        val modelo = findViewById<EditText>(R.id.modeloedittext)
        val spinner = findViewById<Spinner>(R.id.spinnerCategoria)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        val btnSeleccionarImagen = findViewById<Button>(R.id.btnSeleccionarImagen)

        val categorias = listOf(
            "CAS Cajas", "COM Componentes", "KEY Teclados",
            "LAP Laptops", "MON Monitores", "MOU Mouse",
            "PC Computadoras", "SPE Altavoces"
        )

        val categoriasSpinner = spinner

        val categoriaLabels = categorias.map { it.split(" ")[1] }
        val categoriaIds = categorias.map { it.split(" ")[0] }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriaLabels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoriasSpinner.adapter = adapter


        val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->

        if (uri != null) {
                selectedImageUri = uri
                selectedImageName = getFileNameFromUri(uri)
                Toast.makeText(this, "Seleccionaste: $selectedImageName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No se seleccionó imagen", Toast.LENGTH_SHORT).show()
            }
        }

        btnSeleccionarImagen.setOnClickListener {
            getImage.launch("image/*")
        }

        btnGuardar.setOnClickListener {
            val nombreProducto = nombre.text.toString()
            val precioProducto = precio.text.toString()
            val descripcionProducto = descripcion.text.toString()
            val numeroSerieProducto = numeroSerie.text.toString()
            val modeloProducto = modelo.text.toString()

            val categoriaCod = categoriaIds[categoriasSpinner.selectedItemPosition]
            val imagenNombre = selectedImageName ?: "sin_imagen.png"

            val queue = Volley.newRequestQueue(this)
            val url = "http://10.0.2.2/miapp/captura.php"

            val stringRequest = object : StringRequest(
                Request.Method.POST, url,
                { response ->
                    val json = JSONObject(response)
                    val mensaje = json.optString("mensaje_mostrar", "Sin respuesta del servidor")
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                },
                { error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["nombre"] = nombreProducto
                    params["precio"] = precioProducto
                    params["descripcion"] = descripcionProducto
                    params["numbserie"] = numeroSerieProducto
                    params["modelo"] = modeloProducto
                    params["categoria"] = categoriaCod
                    params["imagen"] = imagenNombre
                    return params
                }
            }

            queue.add(stringRequest)
        }

    }

    private fun getFileNameFromUri(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "sin_nombre.png"
    }
}
