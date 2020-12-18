package day10

import java.math.BigInteger
import kotlin.io.path.Path
import kotlin.io.path.readLines

fun List<Int>.totalOptions(): BigInteger {
    val mem = mutableMapOf<Int, BigInteger>()
    fun optionsStartingAt(index: Int): BigInteger = mem.getOrPut(index) {
        val n = this[index]
        val next = (index + 1..index + 3).takeWhile { it < size && this[it] <= n + 3 }
        println("for $n next options are ${next.map { this[it] }}")
        if (next.isEmpty()) 1.toBigInteger() else next.sumOf { optionsStartingAt(it) }
    }
    for (cache in lastIndex downTo 1) optionsStartingAt(cache)
    return optionsStartingAt(0)
}


fun main() {
    val input = Path("src/day10/input.txt").readLines().map { it.toInt() }
 //  (1..64000).scan(1) { acc, _ -> acc + if (Random.nextDouble() < 0.1) 1 else 3 }
    val joltageValues = (input + 0).sorted()
    println(joltageValues)
    val diffs = joltageValues.zipWithNext { a, b -> b - a } //.onEach(::println) // println("$a, $b: ${b - a}")}

    (diffs + 3).groupingBy { it }.eachCount().also(::println)
            .values.fold(1L) { acc, e -> acc * e }.also(::println)

    println("total options: " + joltageValues.totalOptions())
}