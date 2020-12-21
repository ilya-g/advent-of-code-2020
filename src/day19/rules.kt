package day19

import kotlin.io.path.Path
import kotlin.io.path.readText


data class Rule(val id: Int, val match: RuleMatch) {
    override fun toString(): String = "$id: $match"
}
sealed class RuleMatch
data class SimpleMatch(val c: Char) : RuleMatch() {
    override fun toString(): String = "'$c'"
}
data class OrMatch(val anyOf: List<RefMatch>) : RuleMatch() {
    override fun toString(): String = anyOf.joinToString(" | ")
}
data class RefMatch(val sequence: List<Int>) : RuleMatch() {
    override fun toString(): String = sequence.toString()
}

fun parseRule(s: String): Rule {
    val (ids, match) = s.split(": ")
    val id = ids.toInt()
    if (match.startsWith('"')) return Rule(id, SimpleMatch(match.removeSurrounding("\"").single()))
    val anyOf = match.split(" | ").map { RefMatch(it.split(" ").map { it.toInt() }) }
    return Rule(id, anyOf.singleOrNull() ?: OrMatch(anyOf))
}

fun main() {
    val input = Path("src/day19/input.txt").readText().split("\n\n", "\r\n\r\n")

    val rules = input[0].lines().map(::parseRule).sortedBy { it.id }
    val ruleById = rules.associateBy { it.id }.toMutableMap()
    val ruleZero = ruleById[0]!!
    ruleById.values.forEach(::println)


    val singleOneList = listOf(1)
    fun RuleMatch.matchLengths(s: String, start: Int): Collection<Int> {
        return when(this) {
            is SimpleMatch -> if (s.length > start && s[start] == c) singleOneList else emptyList()
            is RefMatch -> {
                var next = setOf(0)
                for (id in sequence) {
                    val r = ruleById[id]!!
                    next = next.flatMapTo(mutableSetOf()) { pos ->
                        r.match.matchLengths(s, start + pos).map { l -> pos + l }
                    }
                }
                next
            }
            is OrMatch -> {
                val allMatches = anyOf.flatMap { it.matchLengths(s, start) }
                allMatches
            }
        }
    }

    fun RuleMatch.matchesEntire(s: String): Boolean = matchLengths(s, 0).any { it == s.length }


    val lines = input[1].lines()

    println(lines.count { ruleZero.match.matchesEntire(it) })

    val correction = """
        8: 42 | 42 8
        11: 42 31 | 42 11 31
    """.trimIndent().lines().map(::parseRule)

    correction.forEach(::println)
    correction.associateByTo(ruleById) { it.id }

    println(lines.count { ruleZero.match.matchesEntire(it) })

}