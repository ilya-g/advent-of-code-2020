package day8

import kotlin.io.path.Path
import kotlin.io.path.readLines

data class Instr(val name: String, val arg: Int)

// TODO: List.replaceAt
fun <T> List<T>.replaceAt(index: Int, value: T): List<T> =
        buildList {
            addAll(this@replaceAt)
            set(index, value)
        }
//        mapIndexed { i, t -> if (i == index) value else t }

fun main() {
    val input = Path("src/day8/input.txt").readLines()

    val instructions = input.map { it.split(" ").let { (n, arg) -> Instr(n, arg.toInt()) }}
//    instructions.forEach(::println)

    val r1 = instructions.execute()
    check(r1 is ExecutionResult.Loops)
    println(r1.valueBeforeLoop)

    outer@
    for (j in instructions.indices.reversed()) {
        val inj = instructions[j]
        val inj2: Instr = when (inj.name) {
            "jmp" -> inj.copy(name = "nop")
            "nop" -> inj.copy(name = "jmp")
            else -> continue@outer
        }
        val result = instructions.replaceAt(j, inj2).execute()
        if (result is ExecutionResult.Terminates) {
            println("Fixed instruction at $j from $inj to $inj2")
            println(result.value)
            break
        }
    }
}

sealed class ExecutionResult {
    data class Terminates(val value: Int) : ExecutionResult()
    data class Loops(val valueBeforeLoop: Int) : ExecutionResult()
}

fun List<Instr>.execute(): ExecutionResult {
    var ip = 0
    var acc = 0
    val executed = mutableSetOf<Int>()
    while (ip < this.size) {
        if (!executed.add(ip)) return ExecutionResult.Loops(acc)
        val i = this[ip]
//        println(i)
        var jmp = 1
        when(i.name) {
            "nop" -> {  }
            "acc" -> { acc += i.arg }
            "jmp" -> { jmp = i.arg }
        }
        ip += jmp
    }
    return ExecutionResult.Terminates(acc)
}