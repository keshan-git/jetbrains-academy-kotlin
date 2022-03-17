package calculator

import java.util.Queue
import java.util.LinkedList
import java.util.Stack

val ASSIGNMENT_REGEX = "[a-zA-Z]+\\s*=\\s*[-]*[a-zA-Z0-9]+".toRegex()
val VARIABLE_REGEX = "[a-zA-Z]+".toRegex()
val OPERATOR_PRESIDENCY = mutableMapOf("+" to 0, "-" to 0, "*" to 1, "/" to 2)

class ExpressionEvaluator {
    private val memory = mutableMapOf<String, Int>()
    private val debug = false

    fun evaluate(expressionString: String): Int {
        val expression: Queue<String> = LinkedList()

        expressionString.split(" ").filter { it.isNotBlank() }.forEach {
            if (isNumber(it) || memory.containsKey(it)) {
                expression.add(it)
            } else if (it.matches("-+".toRegex()) || it.matches("[+]+".toRegex()) || it.matches("[*/()]".toRegex())) {
                expression.add(cleanOp(it))
            } else if (it.startsWith("(")) {
                var text = it
                while (!isNumber(text) && !memory.containsKey(text)) {
                    expression.add("(")
                    text = text.removeRange(0, 1)
                }
                expression.add(text)
            } else if (it.endsWith(")")) {
                var text = it
                var pCount = 0
                while (!isNumber(text) && !memory.containsKey(text)) {
                    pCount++
                    text = text.removeRange(text.length - 1, text.length)
                }
                expression.add(text)
                repeat(pCount) { expression.add(")") }
            } else {
                throw InvalidExpressionException(it)
            }
        }

        if (expression.count { it == "(" } != expression.count { it == ")" }) {
            throw InvalidExpressionException(expressionString)
        }

        if (debug) println("Input $expression")
        return evaluate(expression)
    }

    private fun cleanOp(operator: String): String {
        if (operator.length == 1) {
            return operator
        }

        if (operator.contains("+") && operator.contains("-")) {
            throw InvalidExpressionException(operator)
        }

        return if (operator.length % 2 == 0 || operator.contains("+")) {
            "+"
        } else {
            "-"
        }
    }

    fun assign(userInput: String) {
        if (!userInput.matches(ASSIGNMENT_REGEX)) {
            throw InvalidExpressionException(userInput)
        }
        val (name, value) = userInput.split("=")
        memory[name.trim()] = convertToValue(value.trim())
    }

    fun isVariable(input: String): Boolean {
        return input.matches(VARIABLE_REGEX)
    }

    fun getValue(name: String): Int? {
        return memory[name]
    }

    private fun evaluate(expression: Queue<String>): Int {
        if (expression.size == 1) {
            return expression.poll().toInt()
        }

        //return simpleEvaluate(expression)

        val postfixExpression = convertToPostfix(expression)
        if (debug) println("post $postfixExpression")
        return evaluatePostfix(postfixExpression)

    }

    /**
    1. Add operands (numbers and variables) to the result (postfix notation) as they arrive.
    2. If the stack is empty or contains a left parenthesis on top, push the incoming operator on the stack.
    3. If the incoming operator has higher precedence than the top of the stack, push it on the stack.
    4. If the incoming operator has lower or equal precedence than or to the top of the stack, pop the stack and add operators to the result until you see an operator that has a smaller precedence or a left parenthesis on the top of the stack; then add the incoming operator to the stack.
    5. If the incoming element is a left parenthesis, push it on the stack.
    6. If the incoming element is a right parenthesis, pop the stack and add operators to the result until you see a left parenthesis. Discard the pair of parentheses.
    7. At the end of the expression, pop the stack and add all operators to the result.
    1 1 + 1 - 1 + 1 -
    1 + 1 - 1 + 1 - 1
     */
    private fun convertToPostfix(expression: Queue<String>): Queue<String> {
        val operators = Stack<String>();
        val result: Queue<String> = LinkedList()

        expression.forEach {
            if (isNumber(it) || memory.containsKey(it)) {
                result.add(it)
            } else if (it == "(") {
                operators.push(it)
            } else if (it == ")") {
                while (operators.peek() != "(") {
                    result.add(operators.pop())
                }
                //discard the left parenthesis
                operators.pop()
            } else if (operators.isEmpty() || operators.peek() == "(") {
                operators.push(it)
            } else if (isHigherPres(it, operators.peek())) {
                operators.push(it)
            } else if (!isHigherPres(it, operators.peek())) {
                while (true) {
                    result.add(operators.pop())
                    if (operators.isEmpty() || operators.peek() == "(" || isLowerPres(operators.peek(), it)) break
                }
                operators.push(it)
            }
        }

        while (!operators.isEmpty()) {
            result.add(operators.pop())
        }

        if (result.contains("(") || result.contains(")")) {
            throw InvalidExpressionException(result.toString())
        }

        return result
    }

    /**
    1. If the incoming element is a number, push it into the stack (the whole number, not a single digit!).
    2. If the incoming element is the name of a variable, push its value into the stack.
    3. If the incoming element is an operator, then pop twice to get two numbers and perform the operation; push the result on the stack.
    4. When the expression ends, the number on the top of the stack is a final result.
     */
    private fun evaluatePostfix(postfixExpression: Queue<String>): Int {
        val operands = Stack<Int>();

        postfixExpression.forEach {
            if (isNumber(it) || memory.containsKey(it)) {
                operands.push(convertToValue(it))
            } else if (OPERATOR_PRESIDENCY.contains(it)) {
                val operandY = operands.pop()
                val operandX = operands.pop()
                val temp = calc(operandX, operandY, it)
                if (debug) println("$operandX $it $operandY = $temp")
                operands.push(temp)
            }
        }
        return operands.pop()
    }

    private fun isHigherPres(nextOp: String, currentOp: String): Boolean {
        return OPERATOR_PRESIDENCY[nextOp]!! > OPERATOR_PRESIDENCY[currentOp]!!
    }

    private fun isLowerPres(nextOp: String, currentOp: String): Boolean {
        return OPERATOR_PRESIDENCY[nextOp]!! < OPERATOR_PRESIDENCY[currentOp]!!
    }

    private fun simpleEvaluate(expression: Queue<String>): Int {
        var result = convertToValue(expression.poll())

        do {
            var operator = expression.poll()!!
            var operand = 0
            do {
                val next = expression.poll()!!
                if (isValidOperand(next)) {
                    operand = convertToValue(next)
                    break
                }

                operator = calcOperators(operator, next)
            } while (true)


            result = calc(result, operand, operator)
        } while (expression.isNotEmpty())

        return result
    }

    private fun isValidOperand(operand: String): Boolean {
        return isNumber(operand) || memory.containsKey(operand)
    }

    private fun convertToValue(operand: String): Int {
        if (isNumber(operand)) {
            return operand.toInt()
        } else if (memory.containsKey(operand)) {
            return memory[operand]!!
        }
        throw InvalidExpressionException(operand)
    }

    private fun calc(operandX: Int, operandY: Int, operator: String): Int {
        when (calcOperators(operator)) {
            "+" -> return operandX + operandY
            "-" -> return operandX - operandY
            "*" -> return operandX * operandY
            "/" -> return operandX / operandY
        }
        throw InvalidExpressionException("$operandX $operator $operandY")
    }

    private fun calcOperators(operator: String): String {
        var result = operator[0].toString()
        for (i in 1..operator.lastIndex) {
            result = calcOperators(result, operator[i].toString())
        }
        return result
    }

    private fun calcOperators(currentOperator: String, nextOperator: String): String {
        if (currentOperator == nextOperator) {
            return "+"
        }
        return "-"
    }

    private fun isNumber(input: String): Boolean {
        return try {
            input.toInt()
            true
        } catch (ex: NumberFormatException) {
            false
        }
    }
}

class InvalidExpressionException(name: String) : Exception(name) {

}