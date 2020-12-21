package day20

import common.positionXY.Pos
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.sqrt


class Tile(val id: Int, val data: List<String>, val sideSize: Int)

class Matrix(val xx: Int, val yx: Int, val xy: Int, val yy: Int) {
    val sx = (1 - xx - yx) / 2
    val sy = (1 - xy - yy) / 2
    fun x(x: Int, y: Int, s: Int): Int = x * xx + y * yx + (s - 1) * sx
    fun y(x: Int, y: Int, s: Int): Int = x * xy + y * yy + (s - 1) * sy
}

val rotations = listOf(
    Matrix(1, 0, 0, 1),
    Matrix(0, 1, 1, 0),
    Matrix(-1, 0, 0, 1),
    Matrix(0, -1, 1, 0),
    Matrix(-1, 0, 0, -1),
    Matrix(0, -1, -1, 0),
    Matrix(1, 0, 0, -1),
    Matrix(0, 1, -1, 0),
)

class TileRotation(val tile: Tile, val rotation: Matrix) {
    operator fun get(x: Int, y: Int): Char {
        val xr = rotation.x(x, y, tile.sideSize)
        val yr = rotation.y(x, y, tile.sideSize)
        return tile.data[yr][xr]
    }

    fun line(y: Int) = CharArray(tile.sideSize) { x -> get(x, y) }
    fun lines(): List<CharArray> = (0 until tile.sideSize).map { y -> line(y) }
    override fun toString(): String = (0 until tile.sideSize).joinToString("\n") { y -> line(y).concatToString() }
}

enum class Side {
    U, D, L, R;

    val matching: Side get() = when (this) {
        U -> D
        D -> U
        L -> R
        R -> L
    }
}

operator fun TileRotation.get(side: Side, t: Int): Char = when(side) {
    Side.U -> get(t, 0)
    Side.D -> get(t, tile.sideSize - 1)
    Side.L -> get(0, t)
    Side.R -> get(tile.sideSize - 1, t)
}

val sides = Side.values().toList()

fun TileRotation.matchesWith(other: TileRotation, thisSide: Side): Boolean {
    val otherSide = thisSide.matching
    return (0 until tile.sideSize).all { i -> this[thisSide, i] == other[otherSide, i] }
}

fun Pos.adjacent(side: Side): Pos = when (side) {
    Side.U -> copy(y = y - 1)
    Side.D -> copy(y = y + 1)
    Side.L -> copy(x = x - 1)
    Side.R -> copy(x = x + 1)
}

object Monster {
    val lines = """
                  # 
#    ##    ##    ###
 #  #  #  #  #  #   
    """.trimIndent().lines()
    val height = lines.size
    val width = lines.maxOf { it.length }
    val points = lines.flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, c -> if (c == '#') Pos(x, y) else null }
    }
}

fun TileRotation.monsterAt(x: Int, y: Int): Boolean =
    Monster.points.all { (mx, my) -> get(x + mx, y + my) == '#' }

fun main() {
    val input = Path("src/day20/input.txt").readLines()
    val tileSize = 10

    val tiles = input.chunked(tileSize + 2).map { Tile(it[0].filter { it.isDigit() }.toInt(), it.slice(1..tileSize), tileSize) }
    println(tiles.size)

    val start = Pos(0, 0) to TileRotation(tiles.random(), rotations.random())
    val map = mutableMapOf(start)
    val queue = ArrayDeque(listOf(start))
    val remaining = tiles.toMutableSet().apply { remove(start.second.tile) }

    while (queue.isNotEmpty()) {
        val (pos, tile) = queue.removeFirst()
        for (side in sides) {
            val otherPos = pos.adjacent(side).takeUnless { it in map } ?: continue
            outer@for (otherTile in remaining) {
                for (r in rotations) {
                    val other = TileRotation(otherTile, r)
                    if (tile.matchesWith(other, side)) {
                        map[otherPos] = other
                        queue.addLast(otherPos to other)
                        remaining.remove(otherTile)
                        break@outer
                    }
                }
            }
        }
    }
    val xs = map.keys.minOf { it.x }..map.keys.maxOf { it.x }
    val ys = map.keys.minOf { it.y }..map.keys.maxOf { it.y }

    val corners =
        listOf(xs.first, xs.last).map { x ->
            listOf(ys.first, ys.last).map { y -> map[Pos(x, y)]!! }
        }.flatten().map { it.tile.id }
    println(corners.fold(1L) { acc, id -> acc * id })

    val tilesOnSide = sqrt(tiles.size.toDouble()).toInt()
    val resultData = buildList {
        for (y in 0 until tilesOnSide * tileSize) {
            val py = ys.first + y / tileSize
            val ty = y % tileSize
            if (ty == 0 || ty == (tileSize - 1)) continue
            val line = buildString {
                for (x in 0 until tilesOnSide * tileSize) {
                    val px = xs.first + x / tileSize
                    val tx = x % tileSize
                    if (tx == 0 || tx == (tileSize - 1)) continue
                    append(map[Pos(px, py)]!![tx, ty])
                }
            }
            add(line)
        }
    }
    val result = Tile(0, sideSize = resultData.size, data = resultData)
    println(result.sideSize)


    for (r in rotations) {
        val rr = TileRotation(result, r)
        val monsters = buildList {
            for (y in 0..result.sideSize - Monster.height) {
                for (x in 0..result.sideSize - Monster.width) {
                    if (rr.monsterAt(x, y)) add(Pos(x, y))
                }
            }
        }
        if (monsters.isNotEmpty()) {
            val final = rr.lines()

            for ((x, y) in monsters) {
                for ((mx, my) in Monster.points) {
                    final[y + my][x + mx] = 'O'
                }
            }

            final.forEach { println(it.concatToString()) }

            println(final.sumOf { it.count { c -> c == '#' }})
            break
        }
    }
}