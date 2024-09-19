package com.example.buscaminas

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.widget.GridLayout
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
            loadLayout(R.id.frBuscaminas, R.layout.buscaminas_layout, 8, 8, 10)
        }
        mediumButton.setOnClickListener {
            loadLayout(R.id.frBuscaminas, R.layout.buscaminas_layout, 16, 16, 40)
        }
        hardButton.setOnClickListener {
            loadLayout(R.id.frBuscaminas, R.layout.buscaminas_layout, 24, 24, 99)
        }
    }

    private fun loadLayout(frameLayoutId: Int, layoutId: Int, rows: Int, cols: Int, numMines: Int) {
        val frameLayout = findViewById<FrameLayout>(frameLayoutId)
        frameLayout.removeAllViews() // Elimina la vista anterior si existe
        val layout = layoutInflater.inflate(layoutId, frameLayout, false)
        frameLayout.addView(layout) // Añade el nuevo layout

        setupGrid(rows, cols, numMines) // Configura la nueva cuadrícula
    }

    private fun setupGrid(rows: Int, cols: Int, numMines: Int) {
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        gridLayout.removeAllViews()

        // Crear la matriz
        matrix = Matrix(rows, cols, numMines)

        // Crear botones para cada celda
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val button = Button(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        columnSpec = GridLayout.spec(col, 1f)
                        rowSpec = GridLayout.spec(row, 1f)
                    }
                    text = ""
                    setOnClickListener { handleButtonClick(row, col) }
                }
                gridLayout.addView(button)
            }
        }
    }

    private fun handleButtonClick(row: Int, col: Int) {
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        val button = gridLayout.getChildAt(row * matrix.cols + col) as Button

        if (matrix.revealCell(row, col)) {
            Toast.makeText(this, "¡Juego terminado! Has encontrado una mina.", Toast.LENGTH_SHORT).show()
            button.text = "💣"
        } else {
            val adjacentMines = matrix.getAdjacentMines(row, col)
            button.text = adjacentMines.toString()
        }
    }
}

