package com.example.androidstudio_dsw9

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CarritoAdapter(private val productos: MutableList<Producto>) :
    RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    class CarritoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.textNombreProducto)
        val precio: TextView = itemView.findViewById(R.id.textPrecioProducto)
        val cantidad: TextView = itemView.findViewById(R.id.textCantidad)
        val btnMenos: ImageButton = itemView.findViewById(R.id.btnMenos)
        val btnMas: ImageButton = itemView.findViewById(R.id.btnMas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return CarritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val producto = productos[position]

        // Mostrar datos
        holder.nombre.text = producto.NOMBRE_PRODUCTO
        holder.precio.text = "$${producto.PRECIO_UNITARIO}"
        holder.cantidad.text = producto.CANTIDAD.toString()

        // Aumentar cantidad
        holder.btnMas.setOnClickListener {
            producto.CANTIDAD = producto.CANTIDAD + 1
            holder.cantidad.text = producto.CANTIDAD.toString()
            notifyItemChanged(position)
        }

        // Disminuir cantidad (mínimo 1)
        holder.btnMenos.setOnClickListener {
            if (producto.CANTIDAD > 1) {
                producto.CANTIDAD = producto.CANTIDAD - 1
                holder.cantidad.text = producto.CANTIDAD.toString()
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int = productos.size
}
