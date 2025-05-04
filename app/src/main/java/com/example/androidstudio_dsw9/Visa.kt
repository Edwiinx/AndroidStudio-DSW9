package com.example.androidstudio_dsw9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.androidstudio_dsw9.ui.theme.AndroidStudioDSW9Theme

class Visa : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidStudioDSW9Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PurchaseSummaryScreen()
                }
            }
        }
    }
}

@Composable
fun PurchaseSummaryScreen() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color(0xFFF4F4F4))
            .fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(15.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ItemsSection(modifier = Modifier.weight(1f))
                PaymentSection(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ItemsSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Artículos en tu carrito", fontSize = 20.sp, color = Color.Black)
        Spacer(Modifier.height(16.dp))
        ItemCard("https://via.placeholder.com/70", "Teclado Mecánico", "$25.00", 2)
        ItemCard("https://via.placeholder.com/70", "Monitor MSI", "$45.00", 1)
        ItemCard("https://via.placeholder.com/70", "LIRILI LARILA", "$35.00", 1)
    }
}

@Composable
fun ItemCard(imageUrl: String, name: String, price: String, quantity: Int) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .background(Color(0xFFFAFAFA), shape = RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(),
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .padding(end = 10.dp)
        )

        Column {
            Text(text = name, fontSize = 16.sp, color = Color.Black)
            Text(text = "Precio: $price", fontSize = 14.sp, color = Color.Gray)
            Text(text = "Cantidad: $quantity", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun PaymentSection(modifier: Modifier = Modifier) {
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        Text(text = "Método de Pago", fontSize = 20.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = cardNumber,
            onValueChange = { cardNumber = it },
            label = { Text("Número de Tarjeta") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = expiry,
                onValueChange = { expiry = it },
                label = { Text("Fecha de Vencimiento") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = cvv,
                onValueChange = { cvv = it },
                label = { Text("VCC") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { /* Aquí va la lógica de pago */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4500))
        ) {
            Text(text = "Pagar Ahora", color = Color.White, fontSize = 16.sp)
        }
    }
}
