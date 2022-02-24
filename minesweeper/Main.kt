package minesweeper

fun main() {
    print("How many mines do you want on the field? ")
    val numOfMines = readln().toInt()

    val field = GameField(9, 9, numOfMines)
    field.printField()

    do {
        print("Set/unset mines marks or claim a cell as free: ")
        val (x, y, command) = readLine()!!.split(" ")
        val updated = field.update(command, x.toInt(), y.toInt())
        if (!updated) {
            println("There is a number here!")
            continue
        }

        field.printField()

        when (field.gameState) {
            GameState.WON -> {
                println("Congratulations! You found all the mines!")
                break
            }
            GameState.FAILED -> {
                println("You stepped on a mine and failed!")
                break
            }
            else -> {

            }
        }
    } while (true)
}
