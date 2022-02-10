package cinema

import java.util.*
import kotlin.math.round

const val EMPTY_SEAT = 'S'
const val BOOKED_SEAT = 'B'

const val SMALL_ROOM_LIMIT = 60
const val SMALL_ROOM_PRICE = 10
const val LAGE_ROOM_FRONT_PRICE = 10
const val LAGE_ROOM_BACK_PRICE = 8

var income = 0
fun main() {
    println("Enter the number of rows:")
    val rowCount = readln().toInt()
    println("Enter the number of seats in each row:")
    val seatCount = readln().toInt()
    val layout = parseInput(rowCount, seatCount)

    do {
        when (showMenu()) {
            1 -> printLayout(layout)
            2 -> buyTicket(layout, rowCount, seatCount)
            3 -> printStats(layout, rowCount, seatCount)
            0 -> break
        }
    } while (true)

}

fun printStats(layout: MutableList<MutableList<Char>>, rowCount: Int, seatCount: Int) {
    val totalSeats = rowCount * seatCount
    val ticketsCount = layout.flatten().count { it == BOOKED_SEAT }
    val ticketPerc = ticketsCount * 100.0 / totalSeats
    val totalIncome = calculateIncome(rowCount, seatCount)

    println("\nNumber of purchased tickets: $ticketsCount")
    val formattedPrec = String.format(locale = Locale.ENGLISH, format = "%.2f", ticketPerc)
    println("Percentage: $formattedPrec%")
    println("Current income: $$income")
    println("Total income: $$totalIncome")
}

fun buyTicket(layout: MutableList<MutableList<Char>>, rowCount: Int, seatCount: Int): Unit {
    var row = 0
    var seat = 0
    do {
        try {
            println("\nEnter a row number:")
            row = readln().toInt()
            println("Enter a seat number in that row:")
            seat = readln().toInt()
        } catch (ex: NumberFormatException) {
            println("Wrong input!")
            continue
        }

        val isValid = validate(rowCount, seatCount, row, seat)
        if (!isValid) {
            println("Wrong input!")
            continue
        }

        val isAvail = checkAvail(layout, row, seat)
        if (!isAvail) {
            println("That ticket has already been purchased!")
            continue
        }

        break
    } while (true)

    val price = calculatePrice(rowCount, seatCount, row, seat)
    income += price
    println("Ticket price: $$price")

    bookSeat(layout, row, seat)
}

fun validate(rowCount: Int, seatCount: Int, row: Int, seat: Int): Boolean {
    return row <= rowCount && seat <= seatCount
}

fun checkAvail(layout: MutableList<MutableList<Char>>, row: Int, seat: Int): Boolean {
    return layout[row - 1][seat - 1] == EMPTY_SEAT
}

fun showMenu(): Int {
    val menuContent = """
        
        1. Show the seats
        2. Buy a ticket
        3. Statistics
        0. Exit
    """.trimIndent()
    println(menuContent)
    return readln().toInt()
}

fun bookSeat(layout: MutableList<MutableList<Char>>, row: Int, seat: Int) {
    layout[row - 1][seat - 1] = BOOKED_SEAT
}

fun calculatePrice(rowCount: Int, seatCount: Int, row: Int, seat: Int): Int {
    val totalSeats = rowCount * seatCount
    return if (totalSeats < SMALL_ROOM_LIMIT) {
        SMALL_ROOM_PRICE
    } else if (row <= rowCount / 2) {
        LAGE_ROOM_FRONT_PRICE
    } else {
        LAGE_ROOM_BACK_PRICE
    }
}

fun calculateIncome(rowCount: Int, seatCount: Int): Int {
    val totalSeats = rowCount * seatCount
    return if (totalSeats < SMALL_ROOM_LIMIT) {
        SMALL_ROOM_PRICE * totalSeats
    } else {
        val rowSep = rowCount / 2
        (rowSep * seatCount * LAGE_ROOM_FRONT_PRICE) + ((rowCount - rowSep) * seatCount * LAGE_ROOM_BACK_PRICE)
    }
}

fun parseInput(rowCount: Int, seatCount: Int): MutableList<MutableList<Char>> {
    val layout = mutableListOf<MutableList<Char>>()
    repeat(rowCount) {
        layout.add(MutableList(seatCount) { EMPTY_SEAT })
    }

    return layout
}

fun printLayout(layout: MutableList<MutableList<Char>>) {
    println("Cinema:")
    for (row in 0..layout.lastIndex + 1) {
        if (row == 0) {
            print("  ")
        } else {
            print("$row ")
        }
        for (col in 0..layout[0].lastIndex) {
            if (row == 0) {
                print("${col + 1} ")
            } else {
                print("${layout[row - 1][col]} ")
            }
        }
        println()
    }
}