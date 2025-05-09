package com.example.androidstudio_dsw9

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import java.io.IOException

class CarritoAdapter(
    private val carritoList: MutableList<Producto>,
    private val onCantidadCambiada: () -> Unit,
    private val idUsuario: Int,
    private val onProductoEliminado: () -> Unit
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    class CarritoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.textNombreProducto)
        val precio: TextView = itemView.findViewById(R.id.textPrecioProducto)
        val cantidad: TextView = itemView.findViewById(R.id.textCantidad)
        val imagen: ImageView = itemView.findViewById(R.id.imagenProducto)
        val btnMas: ImageButton = itemView.findViewById(R.id.btnMas)
        val btnMenos: ImageButton = itemView.findViewById(R.id.btnMenos)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carrito, parent, false)
        return CarritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val producto = carritoList[position]
        val context = holder.itemView.context

        holder.nombre.text = producto.NOMBRE_PRODUCTO
        holder.precio.text = "$${producto.PRECIO_UNITARIO}"
        holder.cantidad.text = producto.CANTIDAD.toString()

        // Cargar imagen desde drawable
        val resId = context.resources.getIdentifier(
            producto.IMAGEN.substringBeforeLast(".").lowercase(),
            "drawable",
            context.packageName
        )
        holder.imagen.setImageResource(if (resId != 0) resId else R.drawable.placeholder)

        // SUMAR CANTIDAD
        holder.btnMas.setOnClickListener {
            producto.CANTIDAD += 1
            holder.cantidad.text = producto.CANTIDAD.toString()
            actualizarEnServidor(context, producto)  // Actualizar solo el producto que se está modificando
        }

        // RESTAR CANTIDAD
        holder.btnMenos.setOnClickListener {
            if (producto.CANTIDAD > 1) {
                producto.CANTIDAD -= 1
                holder.cantidad.text = producto.CANTIDAD.toString()
                actualizarEnServidor(context, producto)  // Actualizar solo el producto que se está modificando
            }
        }

        // ELIMINAR DEL CARRITO
        holder.btnEliminar.setOnClickListener {
            eliminarEnServidor(context, producto, position)  // Eliminar solo el producto seleccionado
        }
    }


    private fun eliminarEnServidor(context: android.content.Context, producto: Producto, position: Int) {
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("ID_USUARIO", idUsuario.toString())
            .add("ID_PRODUCTO", producto.ID_PRODUCTO.toString())
            .build()

        val request = Request.Builder()
            .url("http://10.0.2.2/miapp/eliminarproducto.php")  // URL para eliminar el producto
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("CARRITO", "Error al eliminar: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    (context as? Activity)?.runOnUiThread {
                        carritoList.removeAt(position)  // Eliminar producto de la lista
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, carritoList.size)
                        Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show()
                        onProductoEliminado()  // Llamar al callback para recargar carrito
                    }
                }
            }
        })
    }

    private fun actualizarEnServidor(context: android.content.Context, producto: Producto) {
        val client = OkHttpClient()
        val total = producto.CANTIDAD * producto.PRECIO_UNITARIO
        val requestBody = FormBody.Builder()
            .add("ID_USUARIO", idUsuario.toString())
            .add("ID_PRODUCTO", producto.ID_PRODUCTO.toString())
            .add("CANTIDAD", producto.CANTIDAD.toString())
            .add("TOTAL_PRECIO", total.toString())
            .build()

        val request = Request.Builder()
            .url("http://10.0.2.2/miapp/actualizarcarrito.php")  // URL para actualizar la cantidad del producto
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("CARRITO", "Error de conexión: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    (context as? Activity)?.runOnUiThread {
                        Toast.makeText(context, "Cantidad actualizada", Toast.LENGTH_SHORT).show()
                        onCantidadCambiada()  // Llamar al callback para recargar el total
                    }
                }
            }
        })
    }

    override fun getItemCount(): Int = carritoList.size
}
