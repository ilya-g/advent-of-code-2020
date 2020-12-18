package day9

import kotlin.io.path.Path
import kotlin.io.path.readLines

fun List<Long>.findPairOfSum(sum: Long): Pair<Long, Long>? {
    val residuals = mutableMapOf<Long, Long>() // lazy fill
    return asSequence()
            .onEach { a -> residuals[sum - a] = a }
            .mapNotNull { a -> residuals[a]?.takeIf { it != a }?.let { b -> a to b } }.firstOrNull()
}

fun main() {
    val numbers = Path("src/day9/input.txt").readLines().map { it.toLong() }
    val preambleLength = 25
    var outlier: Long = 0
    for (i in preambleLength until numbers.size) {
        val n = numbers[i]
        val prev = numbers.subList(i - preambleLength, i)
        if (prev.findPairOfSum(n) == null) { outlier = n; break }
    }
    println(outlier)

    val prefixSum = numbers.scan(0L) { acc, e -> acc + e }
    for (start in numbers.indices) {
        for (end in start + 2..numbers.size) {
            val sum = prefixSum[end] - prefixSum[start]
            if (sum == outlier) {
                val range = numbers.subList(start, end)
                println(range)
                println(range.minOrNull()!! + range.maxOrNull()!!)
            }
        }
    }
}