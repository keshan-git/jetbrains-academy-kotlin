package cryptography

fun main() {
    val action = Action()
    do {
        println("Task (hide, show, exit):")
        when (val input = readln()) {
            "exit" -> {
                println("Bye!")
                break
            }
            "hide" -> action.hide()
            "show" -> action.show()
            else -> println("Wrong task: $input")
        }
    } while (true)
}

