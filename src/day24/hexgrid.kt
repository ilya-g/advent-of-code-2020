package day24

import kotlin.io.path.Path
import kotlin.io.path.readLines


data class CubePos(val x: Int, val y: Int, val z: Int) {
    init { require(x + y + z == 0) }
    operator fun plus(other: CubePos) = CubePos(this.x + other.x, this.y + other.y, this.z + other.z)
}

enum class Direction(val delta: CubePos) {
    w(CubePos(-1, +1, 0)),
    nw(CubePos(0, +1, -1)),
    ne(CubePos(+1, 0, -1)),

    e(CubePos(+1, -1, 0)),
    se(CubePos(0, -1, +1)),
    sw(CubePos(-1, 0, +1)),
}
val directions = Direction.values().toList()

val directionOptionsRegex = directions.joinToString("|").toRegex()
fun parseDirections(s: String): List<Direction> =
    directionOptionsRegex.findAll(s).map { Direction.valueOf(it.value) }.toList().also {
        check(it.sumOf { it.name.length } == s.length)
    }


fun <Tile> oneCycle(field: Set<Tile>, neighbors: (Tile) -> List<Tile>): Set<Tile> = buildSet<Tile> {
    addAll(field)
    field.forEach { addAll(neighbors(it)) }
    removeAll { e ->
        val n = neighbors(e).count { it in field }
        when {
            e in field -> n == 0 || n > 2 // false -> turn to white
            else -> n != 2
        }
    }
}

fun main() {
    val input = Path("src/day24/input.txt").readLines()

    val tileDirections = input.map(::parseDirections)
//    tiles.forEach(::println)

    val blackTileSet = buildSet<CubePos> {
        tileDirections.forEach { directions ->
            val pos = directions.fold(CubePos(0, 0, 0)) { pos, d -> pos + d.delta }
            if (!add(pos)) remove(pos)
        }
    }

    println(blackTileSet.size)

    (1..100).fold(blackTileSet) { prevSet, _ ->
        oneCycle(prevSet) { tile -> directions.map { tile + it.delta } }
    }.let { println(it.size) }
}