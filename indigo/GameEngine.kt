package indigo

import java.lang.NumberFormatException

private const val TABLE_CARD_DEAL = 4
private const val PLAYER_CARD_DEAL = 6
private const val MAX_CARD_SCORE = 3

class GameEngine {
    private val deck = Deck()
    private val gameAI = GameAI()

    private var computerCardsInHand = mutableListOf<Card>()
    private var playerCardsInHand = mutableListOf<Card>()
    private val cardsOnTable = mutableListOf<Card>()

    private var computerPoints = 0
    private var playerPoints = 0
    private var computerCardsWin = mutableListOf<Card>()
    private var playerCardsWin = mutableListOf<Card>()

    enum class Players { NONE, PLAYER, COMPUTER }

    private var firstPlayer: Players = Players.NONE
    private var lastRoundWinner: Players = Players.NONE

    fun startGame() {
        println("Indigo Card Game")
        do {
            println("Play first?")
            val input = readln()
            when (input.lowercase()) {
                "yes" -> {
                    firstPlayer = Players.PLAYER
                    break
                }
                "no" -> {
                    firstPlayer = Players.COMPUTER
                    break
                }
                else -> continue
            }
        } while (true)

        cardsOnTable.addAll(deck.get(TABLE_CARD_DEAL)!!)
        println("Initial cards on the table: ${getString(cardsOnTable)}\n")

        do {
            if (playerCardsInHand.isEmpty() && computerCardsInHand.isEmpty() && deck.isEmpty()) {
                printTableStatus()
                calculateEndResult()
                printStates()
                endGame()
                break
            }

            if (firstPlayer == Players.PLAYER) {
                val ended = playerTurn()
                if (ended) break
                computerTurn()
            } else {
                computerTurn()
                val ended = playerTurn()
                if (ended) break
            }
        } while (true)
    }

    private fun calculateEndResult() {
        when (lastRoundWinner) {
            Players.PLAYER -> {
                playerCardsWin.addAll(cardsOnTable)
                playerPoints = calculateScore(playerCardsWin)
            }
            Players.COMPUTER -> {
                computerCardsWin.addAll(cardsOnTable)
                computerPoints = calculateScore(computerCardsWin)
            }
            Players.NONE -> {

            }
        }
        cardsOnTable.clear()

        if (playerCardsWin.size > computerCardsWin.size) {
            playerPoints += MAX_CARD_SCORE
        } else {
            computerPoints += MAX_CARD_SCORE
        }
    }

    private fun playerTurn(): Boolean {
        printTableStatus()

        if (playerCardsInHand.isEmpty()) {
            playerCardsInHand = deck.get(PLAYER_CARD_DEAL)!!
        }
        println("Cards in hand: ${getString(playerCardsInHand, format = true)}")

        var cardInput = -1
        do {
            println("Choose a card to play (1-${playerCardsInHand.size}): ")
            val input = readln()
            if (input == "exit") {
                endGame()
                return true
            }

            try {
                val parseInput = input.toInt()
                if (parseInput in 1..playerCardsInHand.size) {
                    cardInput = parseInput
                    break
                }
            } catch (_: NumberFormatException) {

            }
        } while (true)

        val playerCard = playerCardsInHand[cardInput - 1]

        if (cardsOnTable.isNotEmpty() && playerCard.isSameSuitOrRank(cardsOnTable.last())) {
            playerCardsWin.add(playerCard)
            playerCardsWin.addAll(cardsOnTable)

            playerPoints = calculateScore(playerCardsWin)
            printPlayerWins("Player")
            lastRoundWinner = Players.PLAYER
            cardsOnTable.clear()
        } else {
            cardsOnTable.add(playerCard)
        }
        playerCardsInHand.removeAt(cardInput - 1)
        return false
    }

    private fun computerTurn() {
        printTableStatus()

        if (computerCardsInHand.isEmpty()) {
            computerCardsInHand = deck.get(PLAYER_CARD_DEAL)!!
        }

        val computerInput = gameAI.calculateNextMove(computerCardsInHand, cardsOnTable)
        val computerCard = computerCardsInHand[computerInput]
        println(getString(computerCardsInHand))
        println("Computer plays $computerCard\n")

        if (cardsOnTable.isNotEmpty() && computerCard.isSameSuitOrRank(cardsOnTable.last())) {
            computerCardsWin.add(computerCard)
            computerCardsWin.addAll(cardsOnTable)

            computerPoints = calculateScore(computerCardsWin)
            printPlayerWins("Computer")
            lastRoundWinner = Players.COMPUTER
            cardsOnTable.clear()
        } else {
            cardsOnTable.add(computerCard)
        }

        computerCardsInHand.removeAt(computerInput)
    }

    private fun printTableStatus() {
        if (cardsOnTable.isEmpty()) {
            println("No cards on the table")
        } else {
            println("${cardsOnTable.size} cards on the table, and the top card is ${cardsOnTable.last()}")
        }
    }

    private fun endGame() {
        println("Game Over")
    }

    private fun getString(selection: List<Card>?, format: Boolean = false): String {
        if (selection != null) {
            return if (!format) {
                selection.joinToString(" ")
            } else {
                selection.mapIndexed { index, card -> "${index + 1})$card" }.joinToString(" ")
            }
        }
        return ""
    }

    private fun calculateScore(selection: List<Card>): Int {
        return selection.sumOf { card -> card.getScore() }
    }

    private fun printPlayerWins(name: String) {
        println("$name wins cards")
        printStates()
    }

    private fun printStates() {
        println("Score: Player $playerPoints - Computer $computerPoints")
        println("Cards: Player ${playerCardsWin.size} - Computer ${computerCardsWin.size}\n")
    }
}