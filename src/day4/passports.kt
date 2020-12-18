package day4

import kotlin.io.path.Path
import kotlin.io.path.readLines


typealias Passport = MutableMap<String, String>

val eyeColors = setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")

fun isValid(key: String, value: String): Boolean = when (key) {
    "byr" -> value.toInt() in 1920..2002
    "iyr" -> value.toInt() in 2010..2020
    "eyr" -> value.toInt() in 2020..2030
    "hgt" -> when {
        value.endsWith("cm") -> value.dropLast(2).toInt() in 150..193
        value.endsWith("in") -> value.dropLast(2).toInt() in 59..76
        else -> false
    }
    "hcl" -> value.length == 7 && value.startsWith("#") && value.drop(1).all { it in '0'..'9' || it in 'a'..'f' }
    "ecl" -> value in eyeColors
    "pid" -> value.length == 9 && value.all { it.isDigit() }
    "cid" -> true
    else -> error("unknown field $key")
} //.also { if (it == false) println("$key=$value invalid")}

fun main() {
    val lines = Path("src/day4/input.txt").readLines()

    // TODO: 'chunked' by custom condition or 'split' for collections
    val passports = mutableListOf<Passport>()
    var current: Passport = mutableMapOf()
    for (line in lines) {
        if (line.isEmpty() && current.isNotEmpty()) { passports.add(current); current = mutableMapOf(); continue }

        line.split(" ").associateTo(current) { it.split(":").let { (k, v) -> k to v }}
    }
    if (current.isNotEmpty()) passports.add(current)

    passports.forEach(::println)
    val required = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")
    println(passports.count { it.keys.containsAll(required) })
    println(passports.count { it.keys.containsAll(required) && it.all { (k, v) -> isValid(k, v) }})
}