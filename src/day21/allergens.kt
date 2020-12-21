package day21

import kotlin.io.path.Path
import kotlin.io.path.readLines

typealias Ingredient = String
typealias Allergen = String
data class Food(val ingredients: List<Ingredient>, val allergens: List<Allergen>)

val foodRegex = Regex("""([\w\s]+)\(contains (.*)\)""")
fun parseFood(s: String): Food = foodRegex.matchEntire(s)!!.destructured
    .let { (ingredientList, allergenList) ->
        Food(ingredientList.trim().split(" "), allergenList.trim().split(", "))
    }

fun main() {
    val input = Path("src/day21/input.txt").readLines()

    val foods = input.map(::parseFood)

    println("allergen to possible ingredients")
    val allergensToIngredients = foods
        .flatMap { it.allergens.map { a -> a to it.ingredients.toSet() } }
        .groupBy(keySelector = { it.first}, valueTransform = { it.second })
//        .onEach(::println)
        .mapValues { it.value.reduce { acc, e -> acc intersect e }.toMutableSet() }
        .onEach(::println)

    val singles = allergensToIngredients.values.filter { it.size == 1 }.flatten().toMutableSet()
    val remaining = allergensToIngredients.values.filter { it.size > 1 }.toMutableList()
    while (remaining.isNotEmpty()) {
        remaining.removeAll { possible ->
            possible.removeAll(singles)
            if (possible.size == 1) singles.addAll(possible)
            possible.size == 1
        }
    }

    println("allergen to ingredient")
    allergensToIngredients.forEach(::println)

    val ingredientToAllergen = allergensToIngredients.entries.associate { it.value.single() to it.key }

    foods.sumOf { it.ingredients.count { i -> i !in ingredientToAllergen } }
        .let(::println)

    ingredientToAllergen.entries.sortedBy { it.value }.onEach(::println)
        .joinToString(",") { it.key }.let(::println)
}