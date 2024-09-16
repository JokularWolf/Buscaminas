package com.example.buscaminas

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
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
import kotlin.random.Random
import android.widget.GridLayout

class MainActivity : ComponentActivity() {

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
        frameLayout.removeAllViews()
        val layout = layoutInflater.inflate(layoutId, frameLayout, false)
        frameLayout.addView(layout)

        setupGrid(rows, cols, numMines)
    }

    private fun setupGrid(rows: Int, cols: Int, numMines: Int) {
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)

        // Remove any existing buttons
        gridLayout.removeAllViews()

        // Configure GridLayout
        gridLayout.rowCount = rows
        gridLayout.columnCount = cols

        // Create buttons
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

        // Place mines randomly
        placeMines(rows, cols, numMines)
    }

    private fun placeMines(rows: Int, cols: Int, numMines: Int) {
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        val buttons = Array(rows) { row -> Array(cols) { col ->
            gridLayout.getChildAt(row * cols + col) as Button
        }}

        val minePositions = mutableSetOf<Pair<Int, Int>>()
        while (minePositions.size < numMines) {
            val row = Random.nextInt(rows)
            val col = Random.nextInt(cols)
            minePositions.add(row to col)
        }

        buttons.forEachIndexed { row, buttonRow ->
            buttonRow.forEachIndexed { col, button ->
                if (row to col in minePositions) {
                    button.tag = "MINE"
                } else {
                    button.tag = "SAFE"
                }
            }
        }
    }

    private fun handleButtonClick(row: Int, col: Int) {
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        val button = gridLayout.getChildAt(row * 24 + col) as Button // Assumes 24x24; will adjust based on actual size
        if (button.tag == "MINE") {
            Toast.makeText(this, "Game Over! You hit a mine!", Toast.LENGTH_SHORT).show()
            // Handle game over logic here, such as disabling further clicks
        } else {
            button.text = "Safe"
            // Handle safe cell logic, e.g., uncovering adjacent cells
        }
    }
}
