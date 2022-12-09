import java.io.File
import java.lang.RuntimeException
import kotlin.math.abs

fun main() {
    val input = File("input/day09.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private data class Position(val x: Int, val y: Int)

private fun puzzleOne(input: List<String>): Any {
    var head = Position(0, 0)
    var tail = Position(0, 0)
    val positions = mutableSetOf(tail)

    input.forEach { command ->
        val (direction, count) = command.split(" ")
        repeat(count.toInt()) {
            head = moveHead(head, direction)
            tail = moveTail(head, tail)
            positions.add(tail)
        }
    }

    return positions.size
}

private fun puzzleTwo(input: List<String>): Any {
    val knots = Array(10) { _ -> Position(0, 0) }
    val positions = mutableSetOf(knots.last())

    input.forEach { command ->
        val (direction, count) = command.split(" ")
        repeat(count.toInt()) {
            knots[0] = moveHead(knots[0], direction)
            for (i in 1..knots.lastIndex) {
                knots[i] = moveTail(knots[i - 1], knots[i])
            }
            positions.add(knots.last())
        }
    }

    return positions.size
}

private fun moveHead(head: Position, direction: String) = when (direction) {
    "R" -> head.copy(x = head.x + 1)
    "L" -> head.copy(x = head.x - 1)
    "U" -> head.copy(y = head.y + 1)
    "D" -> head.copy(y = head.y - 1)
    else -> throw RuntimeException("Unknown direction $direction")
}

private fun moveTail(head: Position, tail: Position): Position {
    if (abs(head.x - tail.x) < 2 && abs(head.y - tail.y) < 2) {
        return tail
    }
    val x = when {
        head.x > tail.x -> tail.x + 1
        head.x < tail.x -> tail.x - 1
        else -> tail.x
    }
    val y = when {
        head.y > tail.y -> tail.y + 1
        head.y < tail.y -> tail.y - 1
        else -> tail.y
    }
    return Position(x, y)
}
