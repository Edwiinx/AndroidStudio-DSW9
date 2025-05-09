package com.example.androidstudio_dsw9


data class Producto(
    val ID_PRODUCTO: String,
    val ID_CATEGORIA: String,
    val NOMBRE_PRODUCTO: String,
    val MODELO: String,
    val PRECIO_UNITARIO: Double,
    val FECHA_DE_CAPTURA: String,
    var CANTIDAD: Int,
    val ESTADO: String,
    val DESCRIPCION: String,
    val IMAGEN: String,
    val NUMERO_DE_SERIE: String
)
