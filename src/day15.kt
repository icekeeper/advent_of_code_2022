import java.io.File
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.max

fun main() {
    val input = File("input/day15.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private fun puzzleOne(input: List<String>): Any {
    val points = parseInput(input)
    val coverage = calculateCoverage(points, 2_000_000)

    val beacons = points.mapTo(mutableSetOf()) { (_, _, bx, by) -> Pair(bx, by) }
    val beaconsOnTheLine = beacons.count { (bx, by) ->
        by == 2_000_000L && coverage.any { bx in it.first..it.second }
    }

    return coverage.sumOf { it.second - it.first + 1 } - beaconsOnTheLine
}

private fun puzzleTwo(input: List<String>): Any {
    val points = parseInput(input)

    fun findBeacon(max: Long): Pair<Long, Long>? {
        for (y in 0L..max) {
            calculateCoverage(points, y).forEach { (x1, x2) ->
                if (x1 - 1 in 0..max) return Pair(x1 - 1, y)
                if (x2 + 1 in 0..max) return Pair(x2 + 1, y)
            }
        }
        return null
    }

    return findBeacon(4_000_000L)?.let { (x, y) -> x * 4_000_000L + y } ?: "Beacon not found"
}

private fun parseInput(input: List<String>): List<Array<Long>> {
    val pattern = Pattern.compile("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")
    return input.map {
        val matcher = pattern.matcher(it)
        if (!matcher.matches()) {
            throw RuntimeException("Error parsing string: \n$it")
        }
        val sx = matcher.group(1).toLong()
        val sy = matcher.group(2).toLong()
        val bx = matcher.group(3).toLong()
        val by = matcher.group(4).toLong()

        arrayOf(sx, sy, bx, by)
    }
}

private fun calculateCoverage(points: List<Array<Long>>, y: Long): List<Pair<Long, Long>> {
    val coveredLines = points.mapNotNull { (sx, sy, bx, by) ->
        val dist = abs(sx - bx) + abs(sy - by)
        val yDist = abs(y - sy)
        if (yDist > dist) {
            null
        } else {
            Pair(sx - (dist - yDist), sx + (dist - yDist))
        }
    }.sortedBy { it.first }.fold(emptyList<Pair<Long, Long>>()) { acc, pair ->
        if (acc.isEmpty()) {
            listOf(pair)
        } else if (acc.last().second >= pair.first - 1) {
            acc.dropLast(1) + Pair(acc.last().first, max(acc.last().second, pair.second))
        } else {
            acc + pair
        }
    }
    return coveredLines
}