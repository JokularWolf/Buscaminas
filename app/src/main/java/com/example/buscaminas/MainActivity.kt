package com.example.buscaminas

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.widget.GridLayout
import android.widget.PopupMenu
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
            loadLayout(R.id.frBuscaminas, R.layout.buscaminas_layout, 12, 8, 20)
        }
        hardButton.setOnClickListener {
            loadLayout(R.id.frBuscaminas, R.layout.buscaminas_layout, 15, 8, 35)
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
        val cell = matrix.board[row][col]

        // Mostrar el menú emergente solo si el juego no ha terminado
        if (!isGameOver) {
            val popupMenu = PopupMenu(this, button)
            popupMenu.menu.add(0, 1, 0, "Descubrir")
            popupMenu.menu.add(0, 2, 1, "Marcar")

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    1 -> { // Opción "Descubrir"
                        if (!cell.isMarked) {
                            if (matrix.revealCell(row, col)) {
                                // El jugador ha pulsado una mina
                                showGameOver()
                            } else {
                                val adjacentMines = matrix.getAdjacentMines(row, col)
                                button.text = adjacentMines.toString()

                                // Si hay 0 minas adyacentes, revela en cascada
                                if (adjacentMines == 0) {
                                    revealAdjacentCells(row, col)
                                }
                            }
                        } else {
                            Toast.makeText(this, "No puedes descubrir una celda marcada.", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    2 -> { // Opción "Marcar"
                        if (!cell.isRevealed) {
                            cell.isMarked = !cell.isMarked // Alternar marcado
                            button.text = if (cell.isMarked) "🚩" else "" // Mostrar o limpiar el marcador
                        }
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
    }


    private fun revealAdjacentCells(row: Int, col: Int) {
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        val directions = listOf(-1 to -1, -1 to 0, -1 to 1,
            0 to -1,          0 to 1,
            1 to -1, 1 to 0, 1 to 1)

        val queue = mutableListOf(row to col)

        while (queue.isNotEmpty()) {
            val (currentRow, currentCol) = queue.removeAt(0)
            val button = gridLayout.getChildAt(currentRow * matrix.cols + currentCol) as Button

            if (matrix.revealCell(currentRow, currentCol)) {
                // Si se encuentra una mina, no hacemos nada
                continue
            }

            val adjacentMines = matrix.getAdjacentMines(currentRow, currentCol)
            button.text = adjacentMines.toString()

            // Si el número de minas adyacentes es 0, se agrega las celdas adyacentes a la cola
            if (adjacentMines == 0) {
                for (direction in directions) {
                    val newRow = currentRow + direction.first
                    val newCol = currentCol + direction.second

                    // Verifica que las nuevas coordenadas estén dentro del rango
                    if (newRow in 0 until matrix.rows && newCol in 0 until matrix.cols) {
                        val newButton = gridLayout.getChildAt(newRow * matrix.cols + newCol) as Button
                        // Asegúrate de que no se haya revelado ya esta celda
                        if (!matrix.board[newRow][newCol].isRevealed) {
                            queue.add(newRow to newCol)
                        }
                    }
                }
            }
        }
    }

    private var isGameOver = false // Variable para controlar el estado del juego

    private fun showGameOver() {
        isGameOver = true
        Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show()

        // Desactivar todos los botones en el tablero
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        for (i in 0 until matrix.rows) {
            for (j in 0 until matrix.cols) {
                val button = gridLayout.getChildAt(i * matrix.cols + j) as Button
                button.isEnabled = false // Deshabilitar el botón
            }
        }
    }

}
