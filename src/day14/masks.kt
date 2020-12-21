package day14

import kotlin.io.path.Path
import kotlin.io.path.readLines

fun main() {
    val input = Path("src/day14/input.txt").readLines()

    println(processInstructions(input, addressMode = false))

    println(processInstructions(input, addressMode = true))
}


val memRegex = Regex("""mem\[(\d+)] = (\d+)""")

fun processInstructions(input: List<String>, addressMode: Boolean): Long {
    val mem = mutableMapOf<Long, Long>()
    var maskMask: Long = 0
    var maskVal: Long = 0
    for (line in input) {
        when {
            line.startsWith("mask = ") -> {
                val mask = line.removePrefix("mask = ")
                maskVal = mask.replace('X', '0').toLong(2)
                maskMask = mask.replace('1', '0').replace('X', '1').toLong(2)
            }
            line.startsWith("mem") -> {
                val match = memRegex.matchEntire(line)!!
                val baseAddr = match.groupValues[1].toLong()
                val value = match.groupValues[2].toLong()
                if (!addressMode) {
                    mem[baseAddr] = value and maskMask or maskVal
                } else {
                    for (a in decodeAddress(baseAddr, maskMask, maskVal)) {
                        mem[a] = value
                    }
                }
            }
            else -> error(line)
        }
    }
//    println("count = ${mem.size}")
    return mem.values.sum()
}

fun decodeAddress(address: Long, maskMask: Long, maskVal: Long): Sequence<Long> = sequence {
    val base = address and maskMask.inv() or maskVal
//    println("mask: ${maskMask.toString(2).padStart(36, '0')}")
//    println("addr: ${address.toString(2).padStart(36, '0')}")
//    println("base: ${base.toString(2).padStart(36, '0')}")
    val xs = maskMask.countOneBits()
    if (xs == 0) {
        yield(base)
        return@sequence
    }
    val maskBits = maskMask.oneBits()
    for (v in 0L until 1.shl(xs)) {
        var addr = base
        for (bit in 0 until xs) {
            if (v and 1L.shl(bit) != 0L) {
                addr = addr or maskBits[bit]
            }
        }
//        println("addr->${addr.toString(2).padStart(36, '0')}")
        yield(addr)
    }
}

fun Long.oneBits(): List<Long> = buildList {
    var rem = this@oneBits
    while (rem != 0L) {
        val bit = rem.takeLowestOneBit()
        rem = rem and bit.inv()
        add(bit)
    }
}

