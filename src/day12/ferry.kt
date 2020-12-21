package day12

import common.positionXY.Pos
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.abs

enum class Direction { N, E, S, W }
enum class Side { L, R }
val directions: List<Direction> = Direction.values().toList()

sealed class Instruction {
    companion object {
        fun parse(s: String): Instruction {
            val n = s.drop(1).toInt()
            return when (val i = s.take(1)) {
                in "NESW" -> Move(Direction.valueOf(i), n)
                in "LR" -> Turn(Side.valueOf(i), (n / 90).also { require(n % 90 == 0) })
                "F" -> Forward(n)
                else -> error(s)
            }
        }
    }
}
data class Move(val direction: Direction, val n: Int) : Instruction() {
    override fun toString(): String = "$direction$n"
}
data class Turn(val side: Side, val times: Int) : Instruction() {
    override fun toString(): String = "${side}x${times}"
}
data class Forward(val n: Int) : Instruction() {
    override fun toString(): String = "F$n"
}


fun Pos.moveIn(dir: Direction, n: Int): Pos = when (dir) {
    Direction.N -> copy(y = y + n)
    Direction.S -> copy(y = y - n)
    Direction.E -> copy(x = x + n)
    Direction.W -> copy(x = x - n)
}

fun Direction.turn(side: Side): Direction = when(side) {
    Side.R -> directions[(ordinal + 1) % directions.size]
    Side.L -> directions[(ordinal + directions.size - 1) % directions.size]
}

fun Pos.rotate(side: Side): Pos = when(side) {
    Side.R -> Pos(y, -x)
    Side.L -> Pos(-y, x)
}
fun Pos.distance() = abs(x) + abs(y)

fun main() {
    val input = Path("src/day12/input.txt").readLines()

    val moves = input.map(Instruction::parse)
    println(moves)


    var pos = Pos(0, 0)
    var dir = Direction.E
    for (move in moves) {
        when (move) {
            is Forward -> pos = pos.moveIn(dir, move.n)
            is Move -> pos = pos.moveIn(move.direction, move.n)
            is Turn -> {
                repeat(move.times) {
                    dir = dir.turn(move.side)
                }
            }
        }
    }
    println(pos.distance())

    var wp = Pos(10, 1)
    pos = Pos(0, 0)
    for (move in moves) {
        when (move) {
            is Forward -> pos = Pos(pos.x + wp.x * move.n, pos.y + wp.y * move.n)
            is Move -> wp = wp.moveIn(move.direction, move.n)
            is Turn -> repeat(move.times) { wp = wp.rotate(move.side) }
        }
        println("move $move, pos $pos, wp $wp")
    }
    println(pos.distance())

}