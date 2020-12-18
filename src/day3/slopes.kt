package day3

import kotlin.io.path.Path
import kotlin.io.path.readLines

fun main() {
    val lines = Path("src/day3/input.txt").readLines()

    fun countTreesOnSlope(right: Int, down: Int): Int =
        (lines.indices step down).map { r ->
            val c = r / down * right
            val line = lines[r]
            line[c % line.length]
        }.count { it == '#' }

    val slopes = listOf(1 to 1, 3 to 1, 5 to 1, 7 to 1, 1 to 2)

    slopes.map { (r, d) -> countTreesOnSlope(r, d).also { println("r $r, d $d: $it") } }
        .fold(1L, Long::times)
        .let(::println)

}