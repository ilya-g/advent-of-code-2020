package day1_

import kotlin.io.path.Path
import kotlin.io.path.readLines

fun List<Int>.findPairOfSum(sum: Int): Pair<Int, Int>? {
    val residuals = associateBy { sum - it }
    return mapNotNull { a -> residuals[a]?.let { b -> a to b } }.firstOrNull()
}
fun main() {
    val items = Path("src/day1/input.txt").readLines().map { it.toInt() }
    val pair = items.findPairOfSum(2020)
    println(pair)
    println(pair?.let { (a, b) -> a * b })

    val triples = items.associateWith { items.findPairOfSum(2020 - it) }.filterValues { it != null }
    println(triples)

    println(triples.entries.first().let { (k, v) -> k * v!!.first * v.second })
}