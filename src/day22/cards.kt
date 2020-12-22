package day22

import kotlin.io.path.Path
import kotlin.io.path.readLines

typealias Deck = List<Int>
fun readDecks(name: String): Pair<Deck, Deck> {
    val lines = Path(name).readLines()
    val empty = lines.indexOfFirst { it.isEmpty() }
    return lines.take(empty).drop(1).map { it.toInt() } to
           lines.drop(empty + 2).map { it.toInt() }
}

fun main() {
    val (d1, d2) = readDecks("src/day22/input.txt")
    println("Decks")
    println(d1)
    println(d2)

    game1(d1, d2).let {
        printScore("Original game", it)
    }

//    game2(listOf(43, 19), listOf(2, 29, 14))
    game2(d1, d2).let {
        printScore("Recursive game", it)
    }
}

fun List<Int>.score() = asReversed().mapIndexed { index, card -> (index + 1) * card }.sum()

fun printScore(name: String, result: Pair<Int, Deck>) {
    println(name)
    val (winPlayer, winDeck) = result
    println("Winner player $winPlayer: $winDeck")
    println("Score: ${winDeck.score()}")
}


fun game1(p1: Deck, p2: Deck): Pair<Int, Deck> {
    val deck1 = ArrayDeque(p1)
    val deck2 = ArrayDeque(p2)

    while (deck1.isNotEmpty() && deck2.isNotEmpty()) {
        val c1 = deck1.removeFirst()
        val c2 = deck2.removeFirst()

        if (c1 > c2)
            deck1.apply { add(c1); add(c2) }
        else
            deck2.apply { add(c2); add(c1) }
    }

    return if (deck1.isNotEmpty())
        1 to deck1
    else
        2 to deck2
}




data class Snapshot(val s1: Long, val s2: Long)

fun snapshot(d1: Deck, d2: Deck): Snapshot {
    fun Deck.toState(): Long = fold(1L) { acc, e -> 37 * acc + e }
    return Snapshot(d1.toState(), d2.toState())
}

fun game2(p1: Deck, p2: Deck): Pair<Int, Deck> {
    val deck1 = ArrayDeque(p1)
    val deck2 = ArrayDeque(p2)

    val playedRounds = mutableSetOf<Snapshot>()

    while (deck1.isNotEmpty() && deck2.isNotEmpty()) {
        if (!playedRounds.add(snapshot(deck1, deck2))) {
            return 1 to deck1
        }
//        println("Round ${playedRounds.size}:")
//        println(deck1)
//        println(deck2)
        val c1 = deck1.removeFirst()
        val c2 = deck2.removeFirst()

        val p1wins = if (c1 > deck1.size || c2 > deck2.size) {
            c1 > c2
        } else {
            game2(deck1.take(c1), deck2.take(c2)).first == 1
        }

        if (p1wins)
            deck1.apply { add(c1); add(c2) }
        else
            deck2.apply { add(c2); add(c1) }
    }

    return if (deck1.isNotEmpty())
        1 to deck1
    else
        2 to deck2
}

