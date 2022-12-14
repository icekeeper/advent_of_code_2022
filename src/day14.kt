import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min

fun main() {
    val input = File("input/day14.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private data class Point(val x: Int, val y: Int)

private fun puzzleOne(input: List<String>): Any {
    val lines = input.map { line ->
        line.split(" -> ").map {
            val (x, y) = it.split(",").map(String::toInt)
            Point(x, y)
        }
    }

    val maxY = lines.maxOf { line -> line.maxOf { it.y } }
    val rested = mutableMapOf<Int, MutableSet<Int>>()

    fun isOnAnyLine(x: Int, y: Int) = lines.any { line ->
        line.asSequence().windowed(2).any { (p1, p2) ->
            x == p1.x && x == p2.x && y in min(p1.y, p2.y)..max(p1.y, p2.y)
                    || y == p1.y && y == p2.y && x in min(p1.x, p2.x)..max(p1.x, p2.x)
        }
    }

    fun isOccupied(x: Int, y: Int) = rested[x]?.contains(y) ?: false

    fun attemptSandFall(): Boolean {
        var x = 500
        var y = 0
        var canMove = true
        while (canMove && y < maxY) {
            when {
                !isOnAnyLine(x, y + 1) && !isOccupied(x, y + 1) -> y += 1
                !isOnAnyLine(x - 1, y + 1) && !isOccupied(x - 1, y + 1) -> {
                    x -= 1
                    y += 1
                }

                !isOnAnyLine(x + 1, y + 1) && !isOccupied(x + 1, y + 1) -> {
                    x += 1
                    y += 1
                }

                else -> {
                    canMove = false
                    rested.getOrPut(x) { mutableSetOf() }.add(y)
                }
            }
        }
        return !canMove
    }

    var count = 0
    while (attemptSandFall()) {
        count++
    }

    return count
}

private fun puzzleTwo(input: List<String>): Any {
    val lines = input.map { line ->
        line.split(" -> ").map {
            val (x, y) = it.split(",").map(String::toInt)
            Point(x, y)
        }
    }
    val verticalLines = lines.asSequence()
        .flatMap { points -> points.asSequence().windowed(2) }
        .filter { (p1, p2) -> p1.x == p2.x }
        .groupingBy { (p1) -> p1.x }
        .aggregate { _, acc: List<Pair<Int, Int>>?, (p1, p2), _ ->
            (acc ?: emptyList()) + Pair(min(p1.y, p2.y), max(p1.y, p2.y))
        }

    val horizontalLines = lines.asSequence()
        .flatMap { points -> points.asSequence().windowed(2) }
        .filter { (p1, p2) -> p1.y == p2.y }
        .groupingBy { (p1) -> p1.y }
        .aggregate { _, acc: List<Pair<Int, Int>>?, (p1, p2), _ ->
            (acc ?: emptyList()) + Pair(min(p1.x, p2.x), max(p1.x, p2.x))
        }

    val maxY = horizontalLines.keys.max()
    val rested = mutableMapOf<Int, MutableSet<Int>>()

    fun isOnAnyLine(x: Int, y: Int) =
        verticalLines[x]?.any { y in it.first..it.second } ?: false
                || horizontalLines[y]?.any { x in it.first..it.second } ?: false


    fun isOccupied(x: Int, y: Int) = if (y > maxY + 1) true else rested[x]?.contains(y) ?: false

    fun attemptSandFall(): Boolean {
        var x = 500
        var y = 0

        if (isOccupied(x, y)) {
            return false
        }

        var canMove = true
        while (canMove) {
            when {
                !isOnAnyLine(x, y + 1) && !isOccupied(x, y + 1) -> y += 1
                !isOnAnyLine(x - 1, y + 1) && !isOccupied(x - 1, y + 1) -> {
                    x -= 1
                    y += 1
                }

                !isOnAnyLine(x + 1, y + 1) && !isOccupied(x + 1, y + 1) -> {
                    x += 1
                    y += 1
                }

                else -> {
                    canMove = false
                    rested.getOrPut(x) { mutableSetOf() }.add(y)
                }
            }
        }
        return true
    }

    var count = 0
    while (attemptSandFall()) {
        count++
    }

    return count
}
