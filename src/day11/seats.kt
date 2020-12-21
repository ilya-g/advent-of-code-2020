package day11

import kotlin.io.path.Path
import kotlin.io.path.readLines

typealias Field = List<CharArray>
operator fun Field.get(x: Int, y: Int): Char =
        getOrNull(y)?.getOrNull(x) ?: '.'

inline fun Field.mapCells(f: (x: Int, y: Int, c: Char) -> Char): Field =
       mapIndexed { y, r -> CharArray(r.size) { x -> f(x, y, r[x]) } }

inline fun IntRange.firstInSight(f: (d: Int) -> Char): Char? {
    for (d in this) {
        f(d).let { if (it != '.') return it }
    }
    return null
}

inline fun repeatUntilStable(getValue: () -> Any?, action: () -> Unit) {
    var value = getValue()
    while(true) {
        action()
        value = getValue().also { if (value == it) return }
    }
}

fun main() {
    val input = Path("src/day11/input.txt").readLines()

    var map: Field = input.map { it.toCharArray() }


    val rows = map.size
    val cols = map[0].size

    fun printMap() {
        map.forEach { row -> println(row.concatToString()) }
        println()
    }

    fun countSeats() = map.sumOf { row -> row.count { it == '#' } }
    val directions = (-1..1).flatMap { x -> (-1..1).map { y -> x to y } }.filterNot { (x, y) -> x == 0 && y == 0 }

    fun neighbours1(x: Int, y: Int) =
            directions.count { (dx, dy) -> map[x + dx, y + dy] == '#' }

    repeatUntilStable(::countSeats) {
        map = map.mapCells { x, y, c ->
            when {
                c == 'L' && neighbours1(x, y) == 0 -> '#'
                c == '#' && neighbours1(x, y) >= 4 -> 'L'
                else -> c
            }
        }
//        printMap()
//        println(neighbours(2, 0))
    }
    println(countSeats())

    map = map.mapCells { x, y, c -> if (c == '.') c else 'L' }
//    printMap()

    val maxRange = 1..maxOf(rows, cols)
    fun neighbours2(x: Int, y: Int) =
            directions.count { (dx, dy) -> maxRange.firstInSight { d -> map[x + dx * d, y + dy * d] } == '#' }

    repeatUntilStable(::countSeats) {
        map = map.mapCells { x, y, c ->
            when {
                c == 'L' && neighbours2(x, y) == 0 -> '#'
                c == '#' && neighbours2(x, y) >= 5 -> 'L'
                else -> c
            }
        }
    }
    println(countSeats())

}