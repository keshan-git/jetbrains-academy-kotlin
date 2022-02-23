package indigo

enum class Ranks(val symbol: String, val order: Int, val score: Int) {
    RANK_A("A", 0, 1),
    RANK_2("2", 1, 0),
    RANK_3("3", 2, 0),
    RANK_4("4", 3, 0),
    RANK_5("5", 4, 0),
    RANK_6("6", 5, 0),
    RANK_7("7", 6, 0),
    RANK_8("8", 7, 0),
    RANK_9("9", 8, 0),
    RANK_10("10", 9, 1),
    RANK_J("J", 10, 1),
    RANK_Q("Q", 11, 1),
    RANK_K("K", 12, 1)
}

enum class Suits(val symbol: String, val order: Int) {
    SUIT_DIAMOND("♦", 0),
    SUIT_HEART("♥", 0),
    SUIT_SPADES("♠", 0),
    SUIT_CLUBS("♣", 0)
}

class Card(val suit: Suits, val rank: Ranks) {

    override fun toString(): String {
        return rank.symbol + suit.symbol
    }

    fun isSameSuitOrRank(other: Card): Boolean {
        return other.suit == this.suit || other.rank == this.rank
    }

    fun getScore(): Int {
        return rank.score
    }
}