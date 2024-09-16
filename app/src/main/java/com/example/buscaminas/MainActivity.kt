package com.example.buscaminas

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
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
import logica.Matrix

class MainActivity : ComponentActivity() {

    private lateinit var matrix: Matrix

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val easyButton = findViewById<Button>(R.id.btnEasy)
        val mediumButton = findViewById<Button>(R.id.btnMedium)
        val hardButton = findViewById<Button>(R.id.btnHard)

        easyButton.setOnClickListener {
            loadLayout(R.layout.mode_low, 4, 4)
        }
        mediumButton.setOnClickListener {
            loadLayout(R.layout.medium_mode, 8, 8)
        }
        hardButton.setOnClickListener {
            // Implementar lógica para modo difícil si es necesario
        }
    }

    private fun loadLayout(layoutId: Int, rows: Int, cols: Int) {
        val frameLayout = findViewById<FrameLayout>(R.id.frBuscaminas)
        frameLayout.removeAllViews()
        val layout = layoutInflater.inflate(layoutId, frameLayout, false)
        frameLayout.addView(layout)

        val gridLayout = layout.findViewById<GridLayout>(R.id.low_mode_grd)
        matrix = Matrix(gridLayout, rows, cols)

        // Coloca las minas en la matriz lógica
        val numberOfMines = (rows * cols * 0.3).toInt()
        matrix.placeMines(numberOfMines)

        // Crea una matriz de botones
        val buttons = Array(rows) { arrayOfNulls<Button>(cols) }
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val button = gridLayout.getChildAt(i * cols + j) as Button
                buttons[i][j] = button
            }
        }
        // Llama a printMatrix para mostrar la matriz en la consola
        matrix.printMatrix()

        // Actualiza la UI con las minas
        updateUIWithMines(buttons)
    }

    private fun updateUIWithMines(buttons: Array<Array<Button?>>) {
        for (row in 0 until matrix.rows) {
            for (col in 0 until matrix.cols) {
                val button = buttons[row][col]
                if (matrix.get(row, col) == -1) {
                    button?.text = "M"  // Mina
                } else {
                    button?.text = ""   // Espacio vacío o el número de minas cercanas
                }
            }
        }
    }

}





