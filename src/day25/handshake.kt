package day25

const val M = 20201227

fun transforms(subject: Long) = generateSequence(1L) { it * subject % M }
fun transform(subject: Long, loopSize: Int): Long = transforms(subject).elementAt(loopSize)

fun main() {
    val (cardKey, doorKey) = listOf(
        15733400L,
        6408062L
    )

    val remaining = mutableSetOf(cardKey, doorKey)
    val loopSizes = transforms(7).withIndex()
        .takeWhile { remaining.isNotEmpty() }
        .filter { remaining.remove(it.value) }
        .associate { it.value to it.index }

    println(loopSizes)
    println(transform(cardKey, loopSizes[doorKey]!!))
    println(transform(doorKey, loopSizes[cardKey]!!))
}
