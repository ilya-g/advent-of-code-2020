package day7

import kotlin.io.path.Path
import kotlin.io.path.readLines

typealias Color = String

data class Rule(val bagColor: Color, val otherBags: List<Pair<Int, Color>>) {
    companion object {
        private val ruleRegex = Regex("""(\w+ \w+) bags contain (.*)\.""")
        private val itemRegex = Regex("""(\d+) (\w+ \w+) bags?""")
        fun parse(s: String): Rule {
            val (color, contents) = ruleRegex.matchEntire(s)!!.destructured
            if (contents == "no other bags") return Rule(color, emptyList())
            val items = contents
                    .split(", ")
                    .map { itemRegex.matchEntire(it)!!.destructured.let { (n, color) -> n.toInt() to color } }
            return Rule(color, items)
        }
    }
}

fun main() {
    val input = Path("src/day7/input.txt").readLines()

    val rules: Map<Color, Rule> = input.map(Rule::parse).associateBy { it.bagColor }

    fun Rule.canContain(otherColor: Color): Boolean =
            otherBags.any { (_, c) -> c == otherColor || rules[c]!!.canContain(otherColor) }

    fun Rule.sumAllContents(): Int =
            otherBags.sumOf { (n, c) -> n * (1 + rules[c]!!.sumAllContents()) }

    rules.values.count { it.canContain("shiny gold") }.let(::println)

    rules["shiny gold"]!!.sumAllContents().let(::println)
}