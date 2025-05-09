package com.example.androidstudio_dsw9

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CarritoAdapter(private val carritoList: List<Producto>, private val onCantidadCambiada: () -> Unit) :
    RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

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

        // Usar el mismo enfoque para cargar las imágenes
        val nombreImagenSinExtension = producto.IMAGEN.substringBeforeLast(".").lowercase()
        val resId = context.resources.getIdentifier(nombreImagenSinExtension, "drawable", context.packageName)

        if (resId != 0) {
            holder.imagen.setImageResource(resId)
        } else {
            holder.imagen.setImageResource(R.drawable.placeholder) // Imagen por defecto si no se encuentra
        }

        // Acción de incrementar, decrementar o eliminar
        holder.btnMas.setOnClickListener {
            producto.CANTIDAD += 1
            notifyItemChanged(position)
            onCantidadCambiada()
        }

        holder.btnMenos.setOnClickListener {
            if (producto.CANTIDAD > 1) {
                producto.CANTIDAD -= 1
                notifyItemChanged(position)
                onCantidadCambiada()
            }
        }

        holder.btnEliminar.setOnClickListener {
            carritoList.toMutableList().apply {
                removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, carritoList.size)
                onCantidadCambiada()
            }
        }
    }

    override fun getItemCount(): Int = carritoList.size
}
