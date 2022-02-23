package indigo

class Deck {
    private val cards = mutableListOf<Card>()
    private val removed = mutableSetOf<Card>()
    private var topIndex = 0

    init {
        initDeck()
    }

    private fun initDeck() {
        cards.clear()
        removed.clear()
        topIndex = 0

        for (suit in Suits.values()) {
            for (rank in Ranks.values()) {
                cards.add(Card(suit, rank))
            }
        }
        cards.shuffle()
    }

    fun reset() {
        initDeck()
        println("Card deck is reset.")
    }

    fun shuffle() {
        cards.removeAll(removed)
        removed.clear()
        topIndex = 0
        cards.shuffle()
        println("Card deck is shuffled.")
    }

    fun get(numOfCards: Int): MutableList<Card>? {
        if (cards.size >= topIndex + numOfCards) {
            val result = mutableListOf<Card>()
            val selection = cards.subList(topIndex, topIndex + numOfCards)
            result.addAll(selection)

            topIndex += numOfCards
            removed.addAll(selection)
            return result
        }

        println("The remaining cards are insufficient to meet the request.")
        return null
    }

    fun isEmpty(): Boolean {
        return cards.isEmpty() || topIndex >= cards.lastIndex
    }
}