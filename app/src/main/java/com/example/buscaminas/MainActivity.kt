package com.example.buscaminas

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.buscaminas.ui.theme.BuscaminasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        val easyButton = findViewById<Button>(R.id.btnEasy)
        val mediumButton = findViewById<Button>(R.id.btnMedium)
        val hardButton = findViewById<Button>(R.id.btnHard)

        easyButton.setOnClickListener {
            Toast.makeText(this, "Modo Fácil seleccionado", Toast.LENGTH_SHORT).show()
        }
        mediumButton.setOnClickListener {
            Toast.makeText(this, "Modo Medio seleccionado", Toast.LENGTH_SHORT).show()

        }
        hardButton.setOnClickListener {
            Toast.makeText(this, "Modo Dificil seleccionado", Toast.LENGTH_SHORT).show()

        }


    }
}



