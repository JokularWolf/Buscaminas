package com.example.buscaminas

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.widget.GridLayout
import android.widget.PopupMenu
import android.widget.TextView
import logica.Matrix
import android.app.Dialog

class MainActivity : ComponentActivity() {

    private lateinit var matrix: Matrix // Matriz que representa el estado del juego (celdas y minas)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity) // Establece el layout principal de la actividad

        // Encuentra los botones de dificultad en el layout
        val easyButton = findViewById<Button>(R.id.btnEasy)
        val mediumButton = findViewById<Button>(R.id.btnMedium)
        val hardButton = findViewById<Button>(R.id.btnHard)

        // Configura los listeners para cada botón de dificultad
        easyButton.setOnClickListener {
            loadLayout(R.id.frBuscaminas, R.layout.buscaminas_layout, 8, 8, 10) // Carga la cuadrícula fácil
        }
        mediumButton.setOnClickListener {
            loadLayout(R.id.frBuscaminas, R.layout.buscaminas_layout, 12, 8, 20) // Carga la cuadrícula media
        }
        hardButton.setOnClickListener {
            loadLayout(R.id.frBuscaminas, R.layout.buscaminas_layout, 15, 8, 35) // Carga la cuadrícula difícil
        }
    }

    // Método para cargar el layout del juego
    private fun loadLayout(frameLayoutId: Int, layoutId: Int, rows: Int, cols: Int, numMines: Int) {
        val frameLayout = findViewById<FrameLayout>(frameLayoutId) // Obtiene el FrameLayout donde se cargará el juego
        frameLayout.removeAllViews() // Elimina la vista anterior si existe
        val layout = layoutInflater.inflate(layoutId, frameLayout, false) // Infla el nuevo layout
        frameLayout.addView(layout) // Añade el nuevo layout al FrameLayout

        // Reiniciar el estado del juego
        isGameOver = false // Reinicia el estado del juego

        // Configura la cuadrícula con el número de filas, columnas y minas
        setupGrid(rows, cols, numMines)
    }

    // Método para configurar la cuadrícula del juego
    private fun setupGrid(rows: Int, cols: Int, numMines: Int) {
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout) // Obtiene el GridLayout donde se dibujará la cuadrícula
        gridLayout.removeAllViews() // Limpia cualquier vista anterior

        // Crear la matriz que representará el estado del juego
        matrix = Matrix(rows, cols, numMines)

        // Crear botones para cada celda de la cuadrícula
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val button = Button(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0 // Configura el ancho de cada botón
                        height = 0 // Configura la altura de cada botón
                        columnSpec = GridLayout.spec(col, 1f) // Especifica la columna
                        rowSpec = GridLayout.spec(row, 1f) // Especifica la fila
                    }
                    text = "" // Inicialmente, no hay texto en el botón
                    setOnClickListener { handleButtonClick(row, col) } // Maneja el clic en el botón
                }
                gridLayout.addView(button) // Añade el botón al GridLayout
            }
        }
    }

    // Método que maneja el clic en un botón de la cuadrícula
    private fun handleButtonClick(row: Int, col: Int) {
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout) // Obtiene el GridLayout
        val button = gridLayout.getChildAt(row * matrix.cols + col) as Button // Obtiene el botón correspondiente
        val cell = matrix.board[row][col] // Obtiene la celda de la matriz

        if (!isGameOver) { // Solo si el juego no ha terminado
            val popupMenu = PopupMenu(this, button) // Crea un menú emergente
            popupMenu.menu.add(0, 1, 0, "Descubrir") // Opción para descubrir la celda
            popupMenu.menu.add(0, 2, 1, "Marcar") // Opción para marcar la celda

            // Maneja el clic en las opciones del menú
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    1 -> { // Opción "Descubrir"
                        if (!cell.isMarked) { // Solo si la celda no está marcada
                            if (matrix.revealCell(row, col)) { // Si se revela una mina
                                showGameOver() // Muestra el diálogo de fin de juego
                            } else {
                                val adjacentMines = matrix.getAdjacentMines(row, col) // Obtiene el número de minas adyacentes
                                button.text = adjacentMines.toString() // Muestra el número en el botón

                                // Si hay 0 minas adyacentes, revela en cascada
                                if (adjacentMines == 0) {
                                    revealAdjacentCells(row, col) // Revela celdas adyacentes
                                }

                                // Verificar si el jugador ha ganado
                                if (checkWinCondition()) {
                                    showWinDialog() // Muestra el diálogo de victoria
                                }
                            }
                        } else {
                            Toast.makeText(this, "No puedes descubrir una celda marcada.", Toast.LENGTH_SHORT).show() // Mensaje si se intenta descubrir una celda marcada
                        }
                        true
                    }
                    2 -> { // Opción "Marcar"
                        if (!cell.isRevealed) { // Solo si la celda no está revelada
                            cell.isMarked = !cell.isMarked // Alterna el estado de marcado
                            button.text = if (cell.isMarked) "🚩" else "" // Muestra o limpia el marcador

                            // Verificar si el jugador ha ganado después de marcar
                            if (checkWinCondition()) {
                                showWinDialog() // Muestra el diálogo de victoria
                            }
                        }
                        true
                    }
                    else -> false // Si no es ninguna de las opciones
                }
            }

            popupMenu.show() // Muestra el menú emergente
        }
    }

    // Método para revelar las celdas adyacentes cuando no hay minas cerca
    private fun revealAdjacentCells(row: Int, col: Int) {
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout) // Obtiene el GridLayout
        val directions = listOf(-1 to -1, -1 to 0, -1 to 1, // Direcciones para explorar las celdas adyacentes
            0 to -1,          0 to 1,
            1 to -1, 1 to 0, 1 to 1)

        val queue = mutableListOf(row to col) // Cola para almacenar las celdas a revelar

        while (queue.isNotEmpty()) { // Mientras haya celdas en la cola
            val (currentRow, currentCol) = queue.removeAt(0) // Extrae la siguiente celda
            val button = gridLayout.getChildAt(currentRow * matrix.cols + currentCol) as Button // Obtiene el botón correspondiente

            if (matrix.revealCell(currentRow, currentCol)) { // Si se encuentra una mina
                continue // No hace nada
            }

            val adjacentMines = matrix.getAdjacentMines(currentRow, currentCol) // Obtiene el número de minas adyacentes
            button.text = adjacentMines.toString() // Muestra el número en el botón

            // Si el número de minas adyacentes es 0, se agrega las celdas adyacentes a la cola
            if (adjacentMines == 0) {
                for (direction in directions) {
                    val newRow = currentRow + direction.first // Nueva fila
                    val newCol = currentCol + direction.second // Nueva columna

                    // Verifica que las nuevas coordenadas estén dentro del rango
                    if (newRow in 0 until matrix.rows && newCol in 0 until matrix.cols) {
                        val newButton = gridLayout.getChildAt(newRow * matrix.cols + newCol) as Button // Obtiene el nuevo botón
                        // Asegúrate de que no se haya revelado ya esta celda
                        if (!matrix.board[newRow][newCol].isRevealed) {
                            queue.add(newRow to newCol) // Agrega la celda a la cola
                        }
                    }
                }
            }
        }
    }

    private var isGameOver = false // Variable para controlar el estado del juego

    // Método que muestra el diálogo de fin de juego
    private fun showGameOver() {
        isGameOver = true // Marca el juego como terminado

        // Crear el diálogo
        val dialog = Dialog(this) // Crea un nuevo diálogo
        dialog.setContentView(R.layout.dialog_game_over) // Establece el layout del diálogo

        val tvGameOver = dialog.findViewById<TextView>(R.id.tvGameOver) // Encuentra el TextView del diálogo
        val btnRestart = dialog.findViewById<Button>(R.id.btnRestart) // Encuentra el botón de reinicio

        // Configurar el botón de reinicio
        btnRestart.setOnClickListener {
            // Reiniciar el nivel
            loadLayout(R.id.frBuscaminas, R.layout.buscaminas_layout, matrix.rows, matrix.cols, matrix.numMines) // Reinicia el juego
            dialog.dismiss() // Cierra el diálogo
        }

        // Mostrar el diálogo
        dialog.show()

        // Desactivar todos los botones en el tablero
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout) // Obtiene el GridLayout
        for (i in 0 until matrix.rows) {
            for (j in 0 until matrix.cols) {
                val button = gridLayout.getChildAt(i * matrix.cols + j) as Button // Obtiene cada botón
                button.isEnabled = false // Deshabilita el botón
            }
        }
    }

    // Método que muestra el diálogo de victoria
    private fun showWinDialog() {
        val dialog = Dialog(this) // Crea un nuevo diálogo
        dialog.setContentView(R.layout.dialog_you_win) // Establece el layout del diálogo

        val tvYouWin = dialog.findViewById<TextView>(R.id.tvYouWin) // Encuentra el TextView del diálogo
        val btnPlayAgain = dialog.findViewById<Button>(R.id.btnPlayAgain) // Encuentra el botón para jugar de nuevo

        // Configurar el botón para jugar de nuevo
        btnPlayAgain.setOnClickListener {
            // Reiniciar el nivel
            loadLayout(R.id.frBuscaminas, R.layout.buscaminas_layout, matrix.rows, matrix.cols, matrix.numMines) // Reinicia el juego
            dialog.dismiss() // Cierra el diálogo
        }

        // Mostrar el diálogo
        dialog.show() // Muestra el diálogo de victoria
    }

    // Método que verifica si se ha cumplido la condición de victoria
    private fun checkWinCondition(): Boolean {
        var totalCells = matrix.rows * matrix.cols // Total de celdas en la cuadrícula
        var revealedCells = 0 // Contador de celdas reveladas
        var markedMines = 0 // Contador de minas marcadas

        // Itera sobre todas las celdas de la matriz
        for (row in 0 until matrix.rows) {
            for (col in 0 until matrix.cols) {
                val cell = matrix.board[row][col] // Obtiene la celda
                if (cell.isRevealed) { // Si la celda está revelada
                    revealedCells++ // Incrementa el contador de celdas reveladas
                }
                if (cell.isMarked && cell.isMine) { // Si la celda está marcada y es una mina
                    markedMines++ // Incrementa el contador de minas marcadas
                }
            }
        }

        // Verifica si todas las celdas sin mina están reveladas y las minas están marcadas
        return (revealedCells == (totalCells - matrix.numMines)) && (markedMines == matrix.numMines)
    }

}
