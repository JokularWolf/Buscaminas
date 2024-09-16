package logica

import android.widget.GridLayout
import android.widget.Button
import kotlin.random.Random

class Matrix(private val gridLayout: GridLayout, val rows: Int, val cols: Int) {
    private val matrix: Array<Array<Int?>> = Array(rows) { arrayOfNulls(cols) }

    // Inicializa la matriz con valores específicos si es necesario
    init {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                matrix[i][j] = 0 // Puedes cambiar este valor a lo que necesites
            }
        }
        initializeGridLayout()
    }

    // Inicializa los botones en el GridLayout basado en la matriz
    private fun initializeGridLayout() {
        gridLayout.removeAllViews() // Limpia el GridLayout antes de agregar botones
        gridLayout.rowCount = rows
        gridLayout.columnCount = cols

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val button = Button(gridLayout.context).apply {
                    text = "${row + 1},${col + 1}"
                    layoutParams = GridLayout.LayoutParams().apply {
                        rowSpec = GridLayout.spec(row, 1f)
                        columnSpec = GridLayout.spec(col, 1f)
                        width = 0
                        height = 0
                    }
                }
                gridLayout.addView(button)
            }
        }
    }

    // Accede a un elemento de la matriz
    fun get(row: Int, col: Int): Int? {
        return if (row in 0 until rows && col in 0 until cols) matrix[row][col] else null
    }

    // Modifica un elemento de la matriz
    fun set(row: Int, col: Int, value: Int) {
        if (row in 0 until rows && col in 0 until cols) matrix[row][col] = value
    }

    // Genera minas en la matriz
    fun placeMines(numberOfMines: Int) {
        val positions = mutableSetOf<Pair<Int, Int>>()
        while (positions.size < numberOfMines) {
            val row = Random.nextInt(rows)
            val col = Random.nextInt(cols)
            positions.add(row to col)
        }
        positions.forEach { (row, col) ->
            set(row, col, -1) // Usamos -1 para representar una mina
        }
    }

    // Muestra la matriz para depuración
    fun printMatrix() {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val value = matrix[i][j]
                print("${value ?: " "} ") // Imprime un espacio para elementos nulos
            }
            println()
        }
    }

}
