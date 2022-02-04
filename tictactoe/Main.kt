package tictactoe

import java.util.Scanner
import kotlin.math.abs

const val LINE_SEP = "---------"
const val GRID_SEP = "|"
const val PLAYER_1 = 'X'
const val PLAYER_2 = 'O'
const val EMPTY = ' '


fun main() {
    val scanner = Scanner(System.`in`)

//    print("Enter cells: ")
//    val cellInput = scanner.nextLine()
//    val board = parseInput(cellInput)
//    printBoard(board)

    val board = parseInput("")
    printBoard(board)

    var currentPlayer = PLAYER_1
    val finishedStates = mutableListOf("Draw", "X wins", "O wins")

    // Starting of the game loop
    do {
        do {
            print("Enter the coordinates: ")
            val (xPos, yPos) = readln().split(" ").map { it.toIntOrNull() }
            val updateResult = updateBoard(board, xPos, yPos, currentPlayer)
            if (updateResult != null) {
                println(updateResult)
            }
        } while (updateResult != null)
        printBoard(board)

        val result = evaluate(board)
        if (result in finishedStates) {
            println(result)
            break
        }

        currentPlayer = switchPlayer(currentPlayer)
    } while (true)
}

fun switchPlayer(currentPlayer: Char): Char {
    return when (currentPlayer) {
        PLAYER_1 -> PLAYER_2
        PLAYER_2 -> PLAYER_1
        else -> PLAYER_1
    }
}

fun updateBoard(board: MutableList<MutableList<Char>>, xPos: Int?, yPos: Int?, player: Char): String? {
    val validInputs = listOf(1, 2, 3)
    if (xPos != null && yPos != null) {
        return if (xPos in validInputs && yPos in validInputs) {
            if (board[xPos - 1][yPos - 1] == EMPTY) {
                board[xPos - 1][yPos - 1] = player
                null
            } else {
                "This cell is occupied! Choose another one!"
            }
        } else {
            "Coordinates should be from 1 to 3!"
        }
    }
    return "You should enter numbers!"
}

fun parseInput(input: String): MutableList<MutableList<Char>> {
    val board = mutableListOf(
        MutableList(3) { EMPTY },
        MutableList(3) { EMPTY },
        MutableList(3) { EMPTY }
    )

    for (index in input.indices) {
        board[index / 3][index % 3] = input[index]
    }
    return board
}

fun printBoard(board: MutableList<MutableList<Char>>) {
    println(LINE_SEP)
    for (row in 0..2) {
        print("$GRID_SEP ")
        for (col in 0..2) {
            print("${board[row][col]} ")
        }
        println("$GRID_SEP ")
    }
    println(LINE_SEP)
}

fun evaluate(board: MutableList<MutableList<Char>>): String {
    val flatBoard = board.flatten()
    val hasEmpty = flatBoard.contains(EMPTY)
    val p1Count = flatBoard.count { it == PLAYER_1 }
    val p2Count = flatBoard.count { it == PLAYER_2 }
    val p1Win = checkWin(board, PLAYER_1)
    val p2Win = checkWin(board, PLAYER_2)

    val result = if ((p1Win && p2Win) || (abs(p1Count - p2Count) >= 2)) {
        "Impossible"
    } else if (!p1Win and !p2Win) {
        if (hasEmpty) {
            "Game not finished"
        } else {
            "Draw"
        }
    } else if (p1Win) {
        "X wins"
    } else if (p2Win) {
        "O wins"
    } else {
        "Unknown"
    }

    return result
}

fun checkWin(board: MutableList<MutableList<Char>>, playerSymbol: Char): Boolean {
    for (row in board) {
        if (row.all { it == playerSymbol }) {
            return true
        }
    }

    for (cIndex in 0..2) {
        val col = mutableListOf<Char>(
            board[0][cIndex],
            board[1][cIndex],
            board[2][cIndex]
        )
        if (col.all { it == playerSymbol }) {
            return true
        }
    }

    if ((board[0][0] == playerSymbol && board[1][1] == playerSymbol && board[2][2] == playerSymbol) or
        (board[0][2] == playerSymbol && board[1][1] == playerSymbol && board[2][0] == playerSymbol)
    ) {
        return true
    }

    return false
}


