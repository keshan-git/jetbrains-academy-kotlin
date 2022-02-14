package connectfour

import java.lang.NumberFormatException

const val BOARD_RANGE_MIN = 5
const val BOARD_RANGE_MAX = 9
const val BOARD_DEFAULT_ROWS = 6
const val BOARD_DEFAULT_COLS = 7

const val SYMBOL_V_LINE = '║'
const val SYMBOL_H_LINE = '═'
const val SYMBOL_T = '╩'
const val SYMBOL_L_RIGHT = '╚'
const val SYMBOL_L_LEFT = '╝'

const val EMPTY = ' '
const val PLAYER_1 = 'o'
const val PLAYER_2 = '*'

const val END_KEYWORD = "end"

fun main() {
    println("Connect Four")
    println("First player's name:")
    val player1Name = readln()
    println("Second player's name:")
    val player2Name = readln()

    val board = initBoard()

    val numOfGames = initGameCounts()
    val gamesMsg = if (numOfGames == 1) "Single game" else "Total $numOfGames games"

    println("$player1Name VS $player2Name")
    println("${board.size} X ${board[0].size} board")
    println(gamesMsg)

    var gameRound = 0
    val state = GameState()

    gameRounds@while (++gameRound <= numOfGames) {
        if (numOfGames != 1) println("Game #$gameRound")
        printBoard(board)

        var roundP1Name = player1Name
        var roundP2Name = player2Name
        var roundP1Symbol = PLAYER_1
        var roundP2Symbol = PLAYER_2
        if (gameRound % 2 == 0) {
            roundP1Name = player2Name
            roundP2Name = player1Name
            roundP1Symbol = PLAYER_2
            roundP2Symbol = PLAYER_1
        }

        do {
            var isTerminated = takeTurn(board, roundP1Name, roundP1Symbol)
            if (isTerminated) break@gameRounds
            printBoard(board)

            checkBoard(board, state)
            if (state.isDone()) {
                printGameDone(state, player1Name, player2Name)
                break
            }

            isTerminated = takeTurn(board, roundP2Name, roundP2Symbol)
            if (isTerminated) break@gameRounds
            printBoard(board)

            checkBoard(board, state)
            if (state.isDone()) {
                printGameDone(state, player1Name, player2Name)
                break
            }
        } while (true)

        println("Score" )
        println("$player1Name: ${state.p1Score} $player2Name: ${state.p2Score}")
        clearBoard(board)
        resetWin(state)
    }
    displayEndMessage()
}

fun resetWin(state: GameState) {
    state.p1Win = false
    state.p2Win = false
    state.draw = false
}

fun clearBoard(board: MutableList<MutableList<Char>>) {
    for (i in 0 until board.size) {
        for (j in 0 until board[0].size) {
            board[j][i] = EMPTY
        }
    }
}

fun checkBoard(board: MutableList<MutableList<Char>>, gameState: GameState): Unit {
    var status = checkWin(board)

    val transposeBoard = createBoard(board[0].size, board.size)
    for (i in 0 until board.size) {
        for (j in 0 until board[0].size) {
            transposeBoard[j][i] = board[i][j]
        }
    }

    if (!status.first) {
        status = checkWin(transposeBoard)
    }

    if (!status.first) {
        status = checkWinDiagonal(board)
    }

    if (status.first) {
        if (status.second == PLAYER_1) {
            gameState.p1Win = true
            gameState.p1Score += 2
        } else if (status.second == PLAYER_2) {
            gameState.p2Win = true
            gameState.p2Score += 2
        }
    }

    val isDraw = board.flatten().count { it == EMPTY } == 0
    if (isDraw) {
        gameState.draw = true
        gameState.p1Score += 1
        gameState.p2Score += 1
    }
}

fun printGameDone(state: GameState, player1Name: String, player2Name: String) {
    if (state.isWon()) {
        if (state.p1Win) {
            println("Player $player1Name won")
        } else if (state.p2Win) {
            println("Player $player2Name won")
        }
    }

    if (state.draw) {
        println("It is a draw")
    }
}

fun checkWinDiagonal(board: MutableList<MutableList<Char>>): Pair<Boolean, Char> {
    var won = false
    var wonPlayer = EMPTY

    loop@ for (rowIndex in 0..board.lastIndex) {
        for (colIndex in 0..board[rowIndex].lastIndex) {
            if (board[rowIndex][colIndex] == EMPTY) {
                continue
            }

            val upwardCheck = rowIndex >= 3 && colIndex <= board[rowIndex].lastIndex - 3 &&
                    board[rowIndex][colIndex] == board[rowIndex - 1][colIndex + 1] &&
                    board[rowIndex - 1][colIndex + 1] == board[rowIndex - 2][colIndex + 2] &&
                    board[rowIndex - 2][colIndex + 2] == board[rowIndex - 3][colIndex + 3]

            val downwardCheck = rowIndex <= board.lastIndex - 3 && colIndex <= board[rowIndex].lastIndex - 3 &&
                    board[rowIndex][colIndex] == board[rowIndex + 1][colIndex + 1] &&
                    board[rowIndex + 1][colIndex + 1] == board[rowIndex + 2][colIndex + 2] &&
                    board[rowIndex + 2][colIndex + 2] == board[rowIndex + 3][colIndex + 3]

            if (upwardCheck || downwardCheck) {
                won = true
                wonPlayer = board[rowIndex][colIndex]
                break@loop
            }
        }
    }

    return Pair(won, wonPlayer)
}

fun checkWin(board: MutableList<MutableList<Char>>): Pair<Boolean, Char> {
    var won = false
    var wonPlayer = EMPTY

    loop@ for (row in board) {
        var seqCount = 0
        for (colIndex in 0 until row.lastIndex) {
            if (row[colIndex] != EMPTY && row[colIndex] == row[colIndex + 1]) {
                seqCount++
                if (seqCount == 3) {
                    won = true
                    wonPlayer = row[colIndex]
                    break@loop
                }
            } else {
                seqCount = 0
            }
        }
    }

    return Pair(won, wonPlayer)
}

fun takeTurn(board: MutableList<MutableList<Char>>, playerName: String, playerSymbol: Char): Boolean {
    do {
        println("$playerName's turn:")
        val input = readln()

        if (input == END_KEYWORD) {
            displayEndMessage()
            return true
        }

        try {
            val col = input.toInt()
            val maxCol = board[0].size
            if (col in 1..maxCol) {
                val isUpdated = updateBoard(board, col - 1, playerSymbol)
                if (isUpdated) {
                    return false
                }
                println("Column $col is full")
            } else {
                println("The column number is out of range (1 - $maxCol)")
            }
        } catch (ex: NumberFormatException) {
            println("Incorrect column number")
        }
    } while (true)
}

fun updateBoard(board: MutableList<MutableList<Char>>, col: Int, playerSymbol: Char): Boolean {
    var nextPos = board.lastIndex
    for (rowIndex in board.indices) {
        if (board[rowIndex][col] != EMPTY) {
            nextPos = rowIndex - 1
            break
        }
    }

    if (nextPos >= 0) {
        board[nextPos][col] = playerSymbol
        return true
    }

    return false
}

fun displayEndMessage() {
    println("Game over!")
}

fun printBoard(board: MutableList<MutableList<Char>>) {
    board.indices.forEach { rowIndex ->
        if (rowIndex == 0) {
            board[rowIndex].indices.forEach { colIndex -> print(" ${colIndex + 1}") }
            println()
        }

        board[rowIndex].indices.forEach { colIndex ->
            val token = board[rowIndex][colIndex]
            val symbol = when (colIndex) {
                board[rowIndex].lastIndex -> "$SYMBOL_V_LINE$token$SYMBOL_V_LINE"
                else -> "$SYMBOL_V_LINE$token"
            }
            print(symbol)
        }
        println()

        if (rowIndex == board.lastIndex) {
            board[rowIndex].indices.forEach { colIndex ->
                val symbol = when (colIndex) {
                    0 -> "$SYMBOL_L_RIGHT$SYMBOL_H_LINE"
                    board[rowIndex].lastIndex -> "$SYMBOL_T$SYMBOL_H_LINE$SYMBOL_L_LEFT"
                    else -> "$SYMBOL_T$SYMBOL_H_LINE"
                }
                print(symbol)
            }
            println()
        }
    }
}

fun initGameCounts(): Int {
    do {
        println("Do you want to play single or multiple games?")
        println("For a single game, input 1 or press Enter")
        println("Input a number of games:")
        val input = readln()

        if (input.isEmpty()) {
            return 1
        }

        try {
            val numOfGames = input.trim().toInt()
            if (numOfGames > 0) {
                return numOfGames
            }
            println("Invalid input")
        } catch (ex: NumberFormatException) {
            println("Invalid input")
            continue
        }
    } while (true)
}

fun initBoard(): MutableList<MutableList<Char>> {
    do {
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")
        val input = readln()

        var rows = BOARD_DEFAULT_ROWS
        var cols = BOARD_DEFAULT_COLS

        if (input.isEmpty()) {
            return createBoard(rows, cols)
        } else {
            val dimComp = input.split("X", ignoreCase = true, limit = 2)
            if (dimComp.size != 2) {
                println("Invalid input")
                continue
            }

            try {
                rows = dimComp[0].trim().toInt()
                if (rows !in BOARD_RANGE_MIN..BOARD_RANGE_MAX) {
                    println("Board rows should be from 5 to 9")
                    continue
                }

                cols = dimComp[1].trim().toInt()
                if (cols !in BOARD_RANGE_MIN..BOARD_RANGE_MAX) {
                    println("Board columns should be from 5 to 9")
                    continue
                }
            } catch (ex: NumberFormatException) {
                println("Invalid input")
                continue
            }

            return createBoard(rows, cols)
        }
    } while (true)
}

fun createBoard(rows: Int, cols: Int): MutableList<MutableList<Char>> {
    val layout = mutableListOf<MutableList<Char>>()
    repeat(rows) {
        layout.add(MutableList(cols) { EMPTY })
    }

    return layout
}

class GameState {
    var p1Win = false
    var p2Win = false
    var draw = false
    var p1Score = 0
    var p2Score = 0

    fun isDone() = p1Win || p2Win || draw
    fun isWon() = p1Win || p2Win
}
