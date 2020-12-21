package day17

data class Vec4(val x: Int, val y: Int, val z: Int, val w: Int)

fun Vec4.around(neighbors: List<Vec4>): List<Vec4> = neighbors.map { n ->
    Vec4(x + n.x, y + n.y, z + n.z, w + n.w)
}

fun main() {
    val initialState = """
.##.####
.#.....#
#.###.##
#####.##
#...##.#
#######.
##.#####
.##...#.
    """.trimIndent().lines()

    val neighbors3b =
        (-1..1).flatMap { x ->
            (-1..1).flatMap { y ->
                (-1..1).map { z -> Vec4(x, y, z, 0) }
            }
        }

    val zero = Vec4(0, 0, 0, 0)
    val neighbors3 = neighbors3b - zero
    val neighbors4 = neighbors3b.flatMap { (-1..1).map { w -> it.copy(w = w) } } - zero


    val initialField = buildSet<Vec4> {
        initialState.forEachIndexed { y, row ->
            row.forEachIndexed { x, c ->
                if (c == '#') add(Vec4(x, y, 0, 0))
            }
        }
    }

    fun oneCycle(field: Set<Vec4>, neighbors: List<Vec4>): Set<Vec4> = buildSet<Vec4> {
        addAll(field)  // all current active cubes
        field.forEach { addAll(it.around(neighbors)) } // all potentially affected neighbors
        retainAll { e ->
            val n = e.around(neighbors).count { it in field }
            n == 3 || (n == 2 && (e in field))
        }
    }

/*
    // generic approach
    fun <Point> oneCycle(field: Set<Point>, neighbors: Point.() -> List<Point>): Set<Point> = buildSet<Point> {
        addAll(field)
        field.forEach { addAll(it.neighbors()) }
        retainAll { e ->
            val n = e.neighbors().count { it in field }
            n == 3 || (n == 2 && (e in field))
        }
    }
*/

    val field1 = (1..6).fold(initialField) { field, _ -> oneCycle(field, neighbors3) }
    println(field1.size)

    val field2 = (1..6).fold(initialField) { field, _ -> oneCycle(field, neighbors4) }
    println(field2.size)

//    fun <T> Iterable<T>.rangeOf(selector: (T) -> Int): IntRange = minOf(selector)..maxOf(selector)
//    println("xs: ${field2.rangeOf { it.x }}")
//    println("ys: ${field2.rangeOf { it.y }}")
//    println("zs: ${field2.rangeOf { it.z }}")
//    println("ws: ${field2.rangeOf { it.w }}")
}