package day2

import kotlin.io.path.Path
import kotlin.io.path.readLines

fun main() {
    val items = Path("src/day2/input.txt").readLines()
    val passwords = items.map(PasswordWithPolicy::parse)

    println(passwords.count { it.matches() })
    println(passwords.count { it.matches2() })
}


data class PasswordWithPolicy(val password: String, val letter: Char, val range: IntRange) {

    fun matches(): Boolean =
            password.count { it == letter } in range

    fun matches2(): Boolean =
            (password[range.first - 1] == letter) xor (password[range.last - 1] == letter)

    companion object {
        private val regex = Regex("(\\d+)-(\\d+) ([a-z]): ([a-z]+)")
        fun parse(policy: String): PasswordWithPolicy {
            return regex.matchEntire(policy)!!.destructured.let { (s, e, c, p) ->
                PasswordWithPolicy(p, c.single(), s.toInt()..e.toInt())
            }
        }
    }
}

