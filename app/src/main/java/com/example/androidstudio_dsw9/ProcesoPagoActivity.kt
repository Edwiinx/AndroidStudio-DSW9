package com.example.androidstudio_dsw9

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException

class ProcesoPagoActivity : AppCompatActivity() {
    private lateinit var etNumeroTarjeta: EditText
    private lateinit var etFechaVencimiento: EditText
    private lateinit var etVCC: EditText
    private lateinit var btnFinalizarCompra: Button

    private var idUsuario: Int = 0
    private var totalCompra: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proceso_pago)

        etNumeroTarjeta = findViewById(R.id.etNumeroTarjeta)
        etFechaVencimiento = findViewById(R.id.etFechaVencimiento)
        etVCC = findViewById(R.id.etVCC)
        btnFinalizarCompra = findViewById(R.id.btnFinalizarCompra)

        idUsuario = intent.getIntExtra("id_usuario", 0)
        totalCompra = intent.getDoubleExtra("total", 0.0)

        // Limitar longitud
        etNumeroTarjeta.filters = arrayOf(InputFilter.LengthFilter(19)) // 16 + 3 espacios
        etFechaVencimiento.filters = arrayOf(InputFilter.LengthFilter(5)) // MM/AA

        // Formato número tarjeta
        etNumeroTarjeta.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val digitsOnly = s.toString().replace(" ", "")
                val formatted = digitsOnly.chunked(4).joinToString(" ")
                etNumeroTarjeta.setText(formatted)
                etNumeroTarjeta.setSelection(formatted.length.coerceAtMost(etNumeroTarjeta.text.length))

                isFormatting = false
            }
        })

        // Formato fecha vencimiento
        etFechaVencimiento.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val clean = s.toString().replace("/", "")
                val formatted = when {
                    clean.length >= 3 -> clean.substring(0, 2) + "/" + clean.substring(2.coerceAtMost(clean.length))
                    else -> clean
                }

                etFechaVencimiento.setText(formatted)
                etFechaVencimiento.setSelection(formatted.length.coerceAtMost(etFechaVencimiento.text.length))

                isFormatting = false
            }
        })

        btnFinalizarCompra.setOnClickListener {
            val numero = etNumeroTarjeta.text.toString().replace(" ", "")
            val fecha = etFechaVencimiento.text.toString()
            val vcc = etVCC.text.toString()

            if (validarCampos(numero, fecha, vcc)) {
                AlertDialog.Builder(this)
                    .setTitle("Confirmar compra")
                    .setMessage("¿Desea realizar esta compra?")
                    .setPositiveButton("Sí") { _, _ ->
                        enviarDatosAlServidor(numero, fecha, vcc)
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }
    }

    private fun validarCampos(numero: String, fecha: String, vcc: String): Boolean {
        if (!numero.startsWith("4") || numero.length != 16) {
            Toast.makeText(this, "La tarjeta debe comenzar con 4 y tener 16 dígitos", Toast.LENGTH_SHORT).show()
            return false
        }

        val fechaRegex = Regex("^(0[1-9]|1[0-2])/\\d{2}$")
        if (!fecha.matches(fechaRegex)) {
            Toast.makeText(this, "Fecha inválida. Usa formato MM/AA", Toast.LENGTH_SHORT).show()
            return false
        }

        if (vcc.length != 3) {
            Toast.makeText(this, "VCC inválido", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun enviarDatosAlServidor(numero: String, fecha: String, vcc: String) {
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("numero", numero)
            .add("fecha", fecha)
            .add("vcc", vcc)
            .add("id_usuario", idUsuario.toString())
            .add("total", totalCompra.toString())
            .build()

        val request = Request.Builder()
            .url("http://192.168.0.4/miapp/procesar_pago.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ProcesoPagoActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val respuesta = response.body?.string()
                runOnUiThread {
                    when {
                        respuesta?.contains("exito") == true -> mostrarOpciones()
                        respuesta?.contains("fondos_insuficientes") == true -> {
                            Toast.makeText(this@ProcesoPagoActivity, "Fondos insuficientes en la tarjeta", Toast.LENGTH_LONG).show()
                        }
                        respuesta?.contains("tarjeta_invalida") == true -> {
                            Toast.makeText(this@ProcesoPagoActivity, "Tarjeta inválida o datos incorrectos", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            Toast.makeText(this@ProcesoPagoActivity, "Fallo en el pago", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun mostrarOpciones() {
        AlertDialog.Builder(this)
            .setTitle("Pago realizado")
            .setMessage("¿Qué deseas hacer ahora?")
            .setPositiveButton("Ver recibo") { _, _ ->
                val intent = Intent(this, ReciboActivity::class.java)
                intent.putExtra("id_usuario", idUsuario)
                startActivity(intent)
            }
            .setNegativeButton("Volver al catálogo") { _, _ ->
                finish()
            }
            .show()
    }
}
