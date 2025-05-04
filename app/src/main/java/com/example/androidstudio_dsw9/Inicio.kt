package com.example.androidstudio_dsw9

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.androidstudio_dsw9.ui.theme.AndroidStudioDSW9Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidStudioDSW9Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        Text(text = "Pantallas disponibles:", style = MaterialTheme.typography.headlineSmall)

                        Spacer(modifier = Modifier.height(16.dp))

                        NavigationButton("Ir a Captura", Captura::class.java)
                        Spacer(modifier = Modifier.height(8.dp))

                        NavigationButton("Ir a Visa", Visa::class.java)
                        Spacer(modifier = Modifier.height(8.dp))

                        NavigationButton("Ir a Carrito", Carrito::class.java)
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationButton(label: String, destination: Class<*>) {
    val context = LocalContext.current
    Button(
        onClick = {
            context.startActivity(Intent(context, destination))
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label)
    }
}
