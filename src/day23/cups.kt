package day23


fun main() {
    val input = "716892543"
    val initial = input.map { it - '0' }.toMutableList()

    val nextMap1 = (initial.asSequence() + initial.first()).zipWithNext().toMap(mutableMapOf())
    doRounds(nextMap1, 100)
    println(nextMap1.sequenceAfter(1).take(nextMap1.size - 1).joinToString(""))

    val nextMap2 = (initial.asSequence() + ((initial.size + 1)..1_000_000) + initial.first())
        .zipWithNext().toMap(mutableMapOf())
    doRounds(nextMap2, 10_000_000)
    val (n1, n2) = nextMap2.sequenceAfter(1).take(2).toList()
    println("$n1, $n2 = ${n1.toLong() * n2}")

}

fun doRounds(nextMap: MutableMap<Int, Int>, rounds: Int) {
    var current = nextMap.keys.first()
    val min = nextMap.keys.minOrNull()!!
    val max = nextMap.keys.maxOrNull()!!
    repeat(rounds) {
        val c1 = nextMap[current]!!
        val c2 = nextMap[c1]!!
        val c3 = nextMap[c2]!!
        val next = nextMap[c3]!!

        var t = current - 1
        while (true) {
            if (t < min) t = max
            if (t != c1 && t != c2 && t != c3) break else t -= 1
        }
        val nextT = nextMap[t]!!
        nextMap[t] = c1
        nextMap[c3] = nextT
        nextMap[current] = next
        current = next
    }
}

fun Map<Int, Int>.sequenceAfter(n: Int) = generateSequence(this[n]) { this[it] }

