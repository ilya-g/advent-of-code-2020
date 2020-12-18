package day5

import kotlin.io.path.Path
import kotlin.io.path.readLines

data class Seat(val row: Int, val seat: Int) {
    val id = row * 8 + seat
    override fun toString(): String = "Seat($row/$seat, id: $id)"
}

fun parseSeat(pass: String): Seat {
    require(pass.length == 10)
    val row = pass.take(7).replace('B', '1').replace('F', '0').toInt(2)
    val seat = pass.drop(7).replace('R', '1').replace('L', '0').toInt(2)
    return Seat(row, seat)
}

fun main() {
    val passes = Path("src/day5/input.txt").readLines().map { parseSeat(it) }

    println(passes.maxByOrNull { it.id })


    val rows = passes.sortedBy { it.id }.groupBy { it.row }
    val notFullRows = rows.filter { (_, seats) ->
        seats.size < (seats.last().seat - seats.first().seat + 1)
    }
    notFullRows.forEach(::println)

    val emptySeats = notFullRows.flatMap { (row, seats) ->
        (seats.first().seat..seats.last().seat).map { Seat(row, it) } - seats
    }
    emptySeats.forEach(::println)
}