package day23


fun main() {
    val input = "716892543"
    val initial = input.map { it - '0' }
    val firstCup = initial.first()

    val nextMap1 = (initial.asSequence() + firstCup).zipWithNext().toMap().let(::IntLookup)
    doRounds(firstCup, nextMap1, 100)
    println(nextMap1.sequenceAfter(1).take(nextMap1.size - 1).joinToString(""))

    val nextMap2 = (initial.asSequence() + ((initial.size + 1)..1_000_000) + firstCup)
        .zipWithNext().toMap().let(::IntLookup)
    doRounds(firstCup, nextMap2, 10_000_000)
    val (n1, n2) = nextMap2.sequenceAfter(1).take(2).toList()
    println("$n1, $n2 = ${n1.toLong() * n2}")
}

fun doRounds(firstKey: Int, nextLookup: IntLookup, rounds: Int) {
    var current = firstKey
    repeat(rounds) {
        val c1 = nextLookup[current]
        val c2 = nextLookup[c1]
        val c3 = nextLookup[c2]
        val next = nextLookup[c3]

        var t = current - 1
        while (true) {
            if (t < nextLookup.min) t = nextLookup.max
            if (t != c1 && t != c2 && t != c3) break else t -= 1
        }
        val nextT = nextLookup[t]
        nextLookup[t] = c1
        nextLookup[c3] = nextT
        nextLookup[current] = next
        current = next
    }
}

class IntLookup(source: Map<Int, Int>) {
    val size = source.size
    val min = source.keys.minOrNull()!!
    val max = source.keys.maxOrNull()!!
    private val array = IntArray(max - min + 1).also { arr -> source.forEach { (k, v) -> arr[k - min] = v } }
    operator fun get(key: Int) = array[key - min]
    operator fun set(key: Int, value: Int) { array[key - min] = value }
}

fun IntLookup.sequenceAfter(n: Int) = generateSequence(this[n]) { this[it] }
//fun Map<Int, Int>.sequenceAfter(n: Int) = generateSequence(this[n]) { this[it] }

