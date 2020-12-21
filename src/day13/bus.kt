package day13

import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.readLines

fun main() {
    val input = Path("src/day13/input.txt").readLines()
    Locale.getDefault()
    val minwait = input.first().toInt()
    val buses = input[1].split(",").map { it.toIntOrNull() }
    // doesn't work for non-co-primes in input, e.g. listOf(2,null,4)

    val busNumbers = buses.filterNotNull()
    println(busNumbers)

    data class Departure(val number: Int, val time: Int?, val addWait: Int)
    val departures = busNumbers.map { n ->
        val d = minwait / n
        val r = minwait % n
        val w = if (r == 0) 0 else n - r
        Departure(n, d * n + if (r == 0) 0 else n, w)
    }
    println(departures)
    departures.minByOrNull { it.time!! }!!.also(::println)
        .let { it.number * it.addWait }.also(::println)

    val busWaits = buses
        .mapIndexedNotNull { index, n -> n?.let { Departure(n, null, -index) } }

    val m = busNumbers.fold(1L) { acc, n -> acc * n }.also { println("M = $it") }
    println("ri, Mi, Mi_inv")
    busWaits
        .map { (n, _, r) ->
            val mi = m / n
            val minv = inverseMod(mi, n.toLong())
            println("$r, $mi, $minv")
            r * mi * minv
        }
        .sum()
        .let { ((it % m) + m) % m }.also(::println)



}

fun inverseMod(a: Long, b: Long): Long {
    var p = a % b
    var r = 1L
    while (p != 1L) {
        p = (p + a) % b
        r += 1
    }
    return r
}