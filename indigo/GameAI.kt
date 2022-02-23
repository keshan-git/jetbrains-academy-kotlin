package indigo

import kotlin.random.Random

class GameAI {
    fun calculateNextMove(cardsInHand: MutableList<Card>, cardsOnTable: MutableList<Card>): Int {
        // (1) If there is only one card in hand, put it on the table
        if (cardsInHand.size == 1) {
            return 0
        }

        // (3) If there are no cards on the table:
        if (cardsOnTable.isEmpty()) {
            val card = tactic3(cardsInHand)
            return cardsInHand.indexOf(card)
        }

        val candidates = getCandidateCards(cardsInHand, cardsOnTable)
        return if (candidates.size == 1) {
            // (2) If there is only one candidate card, put it on the table
            cardsInHand.indexOf(candidates[0])

        } else if (candidates.isEmpty()) {
            // (4) If there are cards on the table but no candidate cards, use the same tactics as in step 3.
            val card = tactic3(cardsInHand)
            cardsInHand.indexOf(card)

        } else {
            // (5) If there are two or more candidate cards:
            val card = tactic3(candidates)
            cardsInHand.indexOf(card)
        }
    }

    /**
     * (3)
     *
     * If there are cards in hand with the same suit, throw one of them at random.
     *  For example, if the cards in hand are 7♥ 9♥ 8♣ A♠ 3♦ 7♦ Q♥ (multiple ♥, and ♦ suits), the computer will
     *  play one card at random.
     *
     * If there are no cards in hand with the same suit, but there are cards with the same rank
     *  (this situation occurs only when there are 4 or fewer cards in hand), then throw one of them at random
     *  For example, if the cards in hand are 7♦ 7♥ 4♠ K♣, throw one of 7♦ 7♥ at random.
     *
     * If there are no cards in hand with the same suit or rank, throw any card at random.
     *  For example, if the cards in hand are 9♥ 8♣ A♠ 3♦, throw any of them at random.
     */
    private fun tactic3(cards: MutableList<Card>): Card {
        val groupBySuit = cards.groupBy { it.suit }.filterValues { it.size > 1 }
        if (groupBySuit.isNotEmpty()) {
            return groupBySuit.values.first()[0]
        }

        val groupByRank = cards.groupBy { it.rank }.filterValues { it.size > 1 }
        if (groupByRank.isNotEmpty()) {
            return groupByRank.values.first()[0]
        }

        return cards[Random.nextInt(0, cards.lastIndex)]
    }

    private fun getCandidateCards(cardsInHand: MutableList<Card>, cardsOnTable: MutableList<Card>): MutableList<Card> {
        return cardsInHand.filter { card -> card.isSameSuitOrRank(cardsOnTable.last()) }.toMutableList()
    }
}