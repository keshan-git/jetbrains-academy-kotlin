package calculator

class Calculator {
    // private val evaluator = ExpressionEvaluator()
    private val evaluator = BigIntExpressionEvaluator()

    fun start() {
        do {
            val userInput = readln().trim()
            if (userInput.startsWith("/")) {
                val exit = actionCommand(userInput.replace("/", ""))
                if (exit) break
            } else if (userInput.isBlank()) {
                continue
            } else if (userInput.contains("=")) {
                actionAssignment(userInput)
            } else if (evaluator.isVariable(userInput)){
                actionGetValue(userInput)
            } else {
                actionEvaluate(userInput)
            }

        } while (true)
    }

    private fun actionGetValue(userInput: String) {
        val result = evaluator.getValue(userInput)
        if ( result != null) {
            println(result)
        } else {
            println("Unknown variable")
        }
    }

    private fun actionAssignment(userInput: String) {
        try {
            evaluator.assign(userInput)
        } catch (ex: InvalidExpressionException) {
            println("Invalid assignment")
        }
    }

    private fun actionCommand(action: String): Boolean {
        when (action) {
            "exit" -> {
                actionExit()
                return true
            }
            "help" -> {
                actionHelp()
            }
            else -> println("Unknown command")
        }
        return false
    }

    private fun actionEvaluate(userInput: String) {
        try {
            val result = evaluator.evaluate(userInput)
            println(result)
        } catch (ex: InvalidExpressionException) {
            println("Invalid expression")
        }
    }

    private fun actionHelp() {
        println("The program evaluate string expression")
    }

    private fun actionExit() {
        println("Bye!")
    }
}