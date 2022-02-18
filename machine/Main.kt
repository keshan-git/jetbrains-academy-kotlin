package machine

class CoffeeMachine {

    class CoffeeDrink(var name: String, var water: Int, var milk: Int, var coffee: Int, var cost: Int)

    enum class MachineState {
        STAND_BY, COFFEE_SELECTION_IN_PROGRESS, OFF
    }

    companion object CoffeeMachineState {
        var water: Int = 400
        var milk: Int = 540
        var coffee: Int = 120
        var cups: Int = 9
        var money: Int = 550
        var state = MachineState.STAND_BY

        override fun toString(): String {
            return """
            The coffee machine has:
            $water ml of water
            $milk ml of milk
            $coffee g of coffee beans
            $cups disposable cups
            $$money of money
        """.trimIndent()
        }
    }

    private var machineState = CoffeeMachineState

    private val espressoDrink = CoffeeDrink("espresso", 250, 0, 16, 4)
    private val latteDrink = CoffeeDrink("latte", 350, 75, 20, 7)
    private val cappuccinoDrink = CoffeeDrink("cappuccino", 200, 100, 12, 6)
    private val drinkMap = mapOf(1 to espressoDrink, 2 to latteDrink, 3 to cappuccinoDrink)

    fun execute(action: String) {
        if (MachineState.STAND_BY == machineState.state) {
            when (action) {
                "buy" -> {
                    println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:")
                    machineState.state = MachineState.COFFEE_SELECTION_IN_PROGRESS
                }
                "fill" -> executeFillAction()
                "take" -> executeTakeAction()
                "remaining" -> executeDisplayAction()
                "exit" -> machineState.state = MachineState.OFF
            }
        } else if (MachineState.COFFEE_SELECTION_IN_PROGRESS == machineState.state) {
            if (action == "back") {
                machineState.state = MachineState.STAND_BY
            } else {
                val drink = drinkMap[action.toInt()]

                if (drink != null) {
                    makeDrink(drink)
                }
            }
        }
    }

    fun isOn() = machineState.state != MachineState.OFF

    fun greet() {
        if (machineState.state == MachineState.STAND_BY) {
            println("\nWrite action (buy, fill, take):")
        }
    }

    private fun makeDrink(drink: CoffeeDrink) {
        machineState.state = MachineState.STAND_BY
        if (machineState.water < drink.water) {
            println("Sorry, not enough water!")
            return
        } else if (machineState.milk < drink.milk) {
            println("Sorry, not enough milk!")
            return
        } else if (machineState.coffee < drink.coffee) {
            println("Sorry, not enough coffee!")
            return
        } else if (machineState.cups < 1) {
            println("Sorry, not enough cups!")
            return
        }

        println("I have enough resources, making you a coffee!")
        machineState.water -= drink.water
        machineState.milk -= drink.milk
        machineState.coffee -= drink.coffee
        machineState.cups--
        machineState.money += drink.cost
    }

    private fun executeFillAction() {
        println("Write how many ml of water do you want to add:")
        machineState.water += readln().toInt()

        println("Write how many ml of milk do you want to add:")
        machineState.milk += readln().toInt()

        println("Write how many grams of coffee beans do you want to add:")
        machineState.coffee += readln().toInt()

        println("Write how many disposable cups of coffee do you want to add:")
        machineState.cups += readln().toInt()
    }

    private fun executeTakeAction() {
        println("I gave you ${machineState.money}")
        machineState.money = 0
    }

    private fun executeDisplayAction() {
        println()
        println(machineState)
    }
}

fun main() {
    val coffee = CoffeeMachine()

    do {
        coffee.greet()
        val action = readln()
        coffee.execute(action)
    } while (coffee.isOn())

}


