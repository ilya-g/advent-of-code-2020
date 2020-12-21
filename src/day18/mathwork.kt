package day18

import kotlin.io.path.*

enum class Operation(val symbol: String, val invoke: (Long, Long) -> Long) {
    Add("+", Long::plus),
    Mul("*", Long::times);
    companion object {
        private val values = values()
        fun forSymbolOrNull(symbol: String) = values.singleOrNull { it.symbol == symbol }
    }
}

val tokenRegex = Regex("(\\d+)|[*+()]")
fun String.tokens(): Sequence<Any /* = Long | Operation | String */> = tokenRegex.findAll(this).map {
    it.value.toLongOrNull() ?:
    Operation.forSymbolOrNull(it.value) ?:
    it.value as Any
}

fun evalExpr(s: String, notLowerPrecedenceThan: Operation.(Operation) -> Boolean): Long {
//    println(s.tokens().toList())
    val argStack = mutableListOf<Long>()
    val opStack = mutableListOf<Any>()
    fun evalOp(op: Operation) {
        val arg2 = argStack.removeLast()
        val arg1 = argStack.removeLast()
        argStack.add(op.invoke(arg1, arg2))
    }
    for (t in s.tokens()) {
        when (t) {
            is Long -> argStack.add(t)
            is Operation -> {
                while (true) {
                    val op = (opStack.lastOrNull() as? Operation)?.takeIf { it.notLowerPrecedenceThan(t) } ?: break
                    opStack.removeLast()
                    evalOp(op)
                }
                opStack.add(t)
            }
            "(" -> opStack.add(t)
            ")" -> while (true) {
                val op = opStack.removeLastOrNull()?.takeUnless { it == "(" } ?: break
                evalOp(op as Operation)
            }
        }
//        println("$argStack  //  $opStack")
    }
    while (opStack.isNotEmpty()) evalOp(opStack.removeLast() as Operation)
    return argStack.single()
}

fun main() {
    val input = Path("src/day18/input.txt").readLines()
    val samePrecedence: Operation.(Operation) -> Boolean = { true }
    val addPrecedence: Operation.(Operation) -> Boolean = { next -> this == next || this == Operation.Add }

    evalExpr("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))", samePrecedence).also(::println)
    evalExpr("5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))", addPrecedence).also(::println)

    input.map { evalExpr(it, samePrecedence) }.sum().also(::println)
    input.map { evalExpr(it, addPrecedence) }.sum().also(::println)
}