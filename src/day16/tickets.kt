package day16

import kotlin.io.path.Path
import kotlin.io.path.readText

fun main() {
    val input = Path("src/day16/input.txt").readText()
    val groups = input.split("\n\n", "\r\n\r\n").map { it.lines() }

    data class FieldRule(val name: String, val ranges: List<IntRange>) {
        operator fun contains(value: Int): Boolean = ranges.any { value in it }
    }

    val rules = groups[0].map {
        FieldRule(
            it.substringBefore(": "),
            it.substringAfter(": ").split(" or ").map { r ->
                r.split("-").let { (s, e) -> s.toInt()..e.toInt() }
            })
    }
    val your = groups[1][1].split(",").map { it.toInt() }
    val others = groups[2].drop(1).map { it.split(",").map { it.toInt() } }

    rules.forEach(::println)
//    others.forEach(::println)
//    your.let(::println)


    val sum = others.asSequence().flatten().filter { n -> rules.none { n in it } }.sum()
    println(sum)

    val validOthers = others.filter { t -> t.all { f -> rules.any { f in it } } }

    val fields = validOthers.map { it.size }.distinct().single()
    val possible = (0 until fields).map { field ->
        val possibleRules = rules.filter { r -> validOthers.all { t -> t[field] in r } }
        println("Field #$field: possible $possibleRules")
        possibleRules.toMutableList()
    }
    println()

    val singles = possible.filter { it.size == 1 }.flatten().toMutableSet()
    val ambiguous = possible.filter { it.size > 1 }.toMutableList()
    while (ambiguous.isNotEmpty()) {
        ambiguous.removeAll { p ->
            p.removeAll { it in singles }
            if (p.size == 1) singles.add(p.single())
            p.size == 1
        }.also { check(it) { "Cannot infer further: $possible" } }
    }
    val fieldRules = possible.map { it.single() }.withIndex()
    fieldRules.forEach { println("Field #${it.index}: ${it.value.name}") }
    println()

    your.let(::println)
    fieldRules.filter { it.value.name.startsWith("departure") }
        .also { r -> println(r.joinToString { "#${it.index}: ${it.value.name}" }) }
        .map { your[it.index] }.also(::println)
        .fold(1L) { acc, e -> acc * e }.let(::println)

}