package logica

import android.widget.GridLayout
import android.widget.Button
import kotlin.random.Random

class Matrix(val rows: Int, val cols: Int, val numMines: Int) {

    // Matriz para representar el estado del tablero
    val board: Array<Array<Cell>>

    init {
        // Inicializa el tablero con celdas vacías
        board = Array(rows) { row -> Array(cols) { col -> Cell() } }
        placeMines()
    }

    // Clase para representar cada celda
    class Cell {
        var isMine: Boolean = false
        var isRevealed: Boolean = false
        var adjacentMines: Int = 0
        var isMarked: Boolean = false
    }

    // Coloca minas en posiciones aleatorias
    private fun placeMines() {
        val minePositions = mutableSetOf<Pair<Int, Int>>()
        while (minePositions.size < numMines) {
            val row = kotlin.random.Random.nextInt(rows)
            val col = kotlin.random.Random.nextInt(cols)
            minePositions.add(row to col)
        }

        minePositions.forEach { (row, col) ->
            board[row][col].isMine = true
        }

        // Calcula el número de minas adyacentes para cada celda
        calculateAdjacentMines()
    }

    // Calcula el número de minas adyacentes para cada celda
    private fun calculateAdjacentMines() {
        val directions = listOf(-1, 0, 1)
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                if (!board[row][col].isMine) {
                    var adjacentMines = 0
                    for (dx in directions) {
                        for (dy in directions) {
                            if (dx != 0 || dy != 0) {
                                val newRow = row + dx
                                val newCol = col + dy
                                if (newRow in 0 until rows && newCol in 0 until cols && board[newRow][newCol].isMine) {
                                    adjacentMines++
                                }
                            }
                        }
                    }
                    board[row][col].adjacentMines = adjacentMines
                }
            }
        }
    }

    // Función para revelar una celda
    fun revealCell(row: Int, col: Int): Boolean {
        val cell = board[row][col]
        if (cell.isRevealed) return false
        cell.isRevealed = true
        return cell.isMine
    }

    // Función para obtener el número de minas adyacentes de una celda
    fun getAdjacentMines(row: Int, col: Int): Int {
        return board[row][col].adjacentMines
    }
}
