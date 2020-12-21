package day15

fun main() {
    val starting = listOf(5,1,9,18,13,8,0)

    val numbers = sequence {
        val memory = starting.dropLast(1).withIndex().associate { it.value to it.index }.toMutableMap()
        yieldAll(starting.dropLast(1))
        var current = starting.last()
        var turn = starting.lastIndex
        while (true) {
            yield(current)
            val prevTurn = memory[current]
            val value = when (prevTurn) {
                null -> 0
                else -> turn - prevTurn
            }
//            println("current: $current, prevTurn: $prevTurn, turn: $turn")
            memory[current] = turn
            current = value
            turn++
        }
    }

    println(numbers.take(10).toList())
    println(numbers.elementAt(2020 - 1))
    println(numbers.elementAt(30000000 - 1))
}