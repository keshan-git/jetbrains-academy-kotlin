package minesweeper

const val UNEXPLORED_CELL = '.'
const val EXPLORED_NO_MINE_AROUND_CELL = '/'
const val EXPLORED_MINE_AROUND_CELL = '#'
const val MINE_CELL = 'X'
const val UNEXPLORED_MARKED_CELL = '*'

enum class GameState {NEW, RUNNING, FAILED, WON}

class GameField(private val rows: Int, private val cols: Int, private val mineCount: Int) {
    private var board: MutableList<MutableList<Char>> = mutableListOf()
    private var mineCounts: MutableList<MutableList<Int>> = mutableListOf()
    private var userMarks: MutableList<MutableList<Boolean>> = mutableListOf()

    var gameState = GameState.NEW

    init {
        repeat(rows) {
            this.board.add(MutableList(cols) { UNEXPLORED_CELL })
            this.mineCounts.add(MutableList(cols) { 0 })
            this.userMarks.add(MutableList(cols) { false })
        }

        // Generate Mines
        val locations = (1..rows * cols).shuffled()
        (0 until mineCount).map { locations[it] - 1 }.forEach { pos ->
            // println("$pos row=${pos / rows} col=${pos % cols}")
            board[pos / rows][pos % cols] = MINE_CELL
        }

        // Calculate Mine counts
        board.forEachIndexed { rowIdx, row ->
            row.forEachIndexed { colIdx, _ ->
                val mines = getAdjacentCells(rowIdx, colIdx).count { it == MINE_CELL }
                mineCounts[rowIdx][colIdx] = mines
            }
        }
    }

    private fun getAdjacentCellsIdx(rowIdx: Int, colIdx: Int): MutableList<Pair<Int, Int>> {
        val adjCellIdx = mutableListOf<Pair<Int, Int>>()
        (-1..1).forEach { rowAdj ->
            (-1..1).forEach { colAdj ->
                adjCellIdx.add(Pair(rowIdx + rowAdj, colIdx + colAdj))
            }
        }
        return adjCellIdx
    }

    private fun getAdjacentCells(rowIdx: Int, colIdx: Int): MutableList<Char> {
        return getAdjacentCellsIdx(rowIdx, colIdx).map { safeGet(it.first, it.second) }.toMutableList()
    }

    private fun safeGet(rowIdx: Int, colIdx: Int): Char {
        if (checkBounds(rowIdx, colIdx)) {
            return board[rowIdx][colIdx]
        }
        return UNEXPLORED_CELL
    }

    private fun safeSet(rowIdx: Int, colIdx: Int, value: Char) {
        if (checkBounds(rowIdx, colIdx)) {
            board[rowIdx][colIdx] = value
        }
    }

    private fun checkBounds(rowIdx: Int, colIdx: Int): Boolean {
        return rowIdx >= 0 && colIdx >= 0 && rowIdx <= board.lastIndex && colIdx <= board[rowIdx].lastIndex
    }

    fun printField() {
        var header = " |${(1..cols).joinToString("")}|"
        header += "\n—|${"-".repeat(cols)}|"
        println(header)

        board.forEachIndexed { rowIdx, row ->
            var resultLine = "${rowIdx + 1}|"
            row.forEachIndexed { colIdx, cell ->
                if (cell == EXPLORED_NO_MINE_AROUND_CELL && mineCounts[rowIdx][colIdx] > 0) {
                    resultLine += mineCounts[rowIdx][colIdx]
                } else if (userMarks[rowIdx][colIdx] && cell != EXPLORED_NO_MINE_AROUND_CELL) {
                    resultLine += UNEXPLORED_MARKED_CELL
                } else if (cell == MINE_CELL) {
                    resultLine += if (gameState == GameState.FAILED) MINE_CELL else UNEXPLORED_CELL
                } else {
                    resultLine += cell
                }
            }
            resultLine += "|"
            println(resultLine)
        }

        val footer = "—|${"-".repeat(cols)}|"
        println(footer)
    }

    fun update(command: String, x: Int, y: Int): Boolean {
        // x-cols y-rows
        val rowIdx = y - 1
        val colIdx = x - 1

        gameState = GameState.RUNNING
        when (command) {
            "free" -> markAsFree(rowIdx, colIdx)
            "mine" -> markAsMine(rowIdx, colIdx)
        }
        evaluate()
        return true
    }

    private fun markAsFree(rowIdx: Int, colIdx: Int) {
        /*
          If the cell is empty and has no mines around, all the cells around it, including the marked ones,
          can be explored, and it should be done automatically. Also, if next to the explored cell there
          is another empty one with no mines around, all the cells around it should be explored as well,
          and so on, until no more can be explored automatically.
        */
        markAsFreeRec(rowIdx, colIdx)

        /*
          If a cell is empty and has mines around it, only that cell is explored,
          revealing a number of mines around it.
        */
        if (board[rowIdx][colIdx] == UNEXPLORED_CELL && mineCounts[rowIdx][colIdx] > 0) {
            safeSet(rowIdx, colIdx, EXPLORED_NO_MINE_AROUND_CELL)
        }

        /*
          If the explored cell contains a mine, the game ends and the player loses.
        */
        if (board[rowIdx][colIdx] == MINE_CELL) {
            gameState = GameState.FAILED
        }
    }

    private fun markAsFreeRec(rowIdx: Int, colIdx: Int) {
        if (checkBounds(rowIdx, colIdx) && board[rowIdx][colIdx] == UNEXPLORED_CELL && mineCounts[rowIdx][colIdx] == 0) {
            board[rowIdx][colIdx] = EXPLORED_NO_MINE_AROUND_CELL
            getAdjacentCellsIdx(rowIdx, colIdx).forEach {
                markAsFreeRec(it.first, it.second)
                safeSet(it.first, it.second, EXPLORED_NO_MINE_AROUND_CELL)
            }
        }
        return
    }

    private fun markAsMine(rowIdx: Int, colIdx: Int) {
        userMarks[rowIdx][colIdx] = !userMarks[rowIdx][colIdx]
    }

    fun setMineMark(x: Int, y: Int): Boolean {
        // x-cols y-rows
        val rowIdx = y - 1
        val colIdx = x - 1

        if (board[rowIdx][colIdx] == MINE_CELL) {
            userMarks[rowIdx][colIdx] = !userMarks[rowIdx][colIdx]
        } else if (mineCounts[rowIdx][colIdx] > 0) {
            return false
        } else {
            userMarks[rowIdx][colIdx] = !userMarks[rowIdx][colIdx]
        }
        return true
    }

    private fun evaluate(): Boolean {
        val userMarksCount = userMarks.flatten().count { it }
        if (userMarksCount == mineCount) {
            board.forEachIndexed { rowIdx, row ->
                row.forEachIndexed { colIdx, cell ->
                    if (cell == MINE_CELL && !userMarks[rowIdx][colIdx]) {
                        return false
                    }
                }
            }
            gameState = GameState.WON
            return true
        }
        return false
    }
}