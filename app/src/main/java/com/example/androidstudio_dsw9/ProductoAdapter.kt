package com.example.androidstudio_dsw9

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import okhttp3.*
import java.io.IOException

class ProductoAdapter(
    private val productos: List<Producto>,
    private val idUsuario: Int
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.textNombreProducto)
        val precio: TextView = itemView.findViewById(R.id.textPrecioProducto)
        val botonAgregar: ImageButton = itemView.findViewById(R.id.btnAgregarCarrito)
        val imagen: ImageView = itemView.findViewById(R.id.imagenProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        val context = holder.itemView.context

        holder.nombre.text = producto.NOMBRE_PRODUCTO
        holder.precio.text = "$${producto.PRECIO_UNITARIO}"

        val nombreImagenSinExtension = producto.IMAGEN.substringBeforeLast(".").lowercase()

        val resId = context.resources.getIdentifier(
            nombreImagenSinExtension, "drawable", context.packageName
        )

        if (resId != 0) {
            holder.imagen.setImageResource(resId)
        } else {
            holder.imagen.setImageResource(R.drawable.placeholder) // Imagen por defecto si no se carga la imagen o no esta en la carpeta drawable
        }

        // Acción al presionar botón agregar
        holder.botonAgregar.setOnClickListener {
            val idProducto = producto.ID_PRODUCTO
            val cantidad = 1
            val totalPrecio = producto.PRECIO_UNITARIO.toString()

            val url = "http://192.168.0.4/miapp/añadir_carrito.php" //URL para realizar consulta a base de datos

            val requestBody = FormBody.Builder()
                .add("ID_USUARIO", idUsuario.toString())
                .add("ID_PRODUCTO", idProducto.toString())
                .add("CANTIDAD", cantidad.toString())
                .add("TOTAL_PRECIO", totalPrecio)
                .build()

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("CARRITO", "Error al conectar: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        (context as? android.app.Activity)?.runOnUiThread {
                            Toast.makeText(context, "Producto agregado al carrito", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("CARRITO", "Error en la respuesta del servidor")
                    }
                }
            })
        }
    }

    override fun getItemCount(): Int = productos.size
}
