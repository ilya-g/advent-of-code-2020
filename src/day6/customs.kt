package day6

import kotlin.io.path.Path
import kotlin.io.path.readText

fun main() {
    val input = Path("src/day6/input.txt").readText()
    val groups = input.split("\n\n", "\r\n\r\n")

    val groupSets = groups.map {
        it.lines().flatMap { s -> s.asIterable() }.toSet()
    }

    println(groupSets.sumOf {it.size})

    val groupSets2 = groups.map {
        it.lines().map { s -> s.toSet() }.reduce { acc, s -> acc intersect s }
    }

    println(groupSets2.sumOf {it.size})

}