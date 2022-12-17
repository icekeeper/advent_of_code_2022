import java.io.File
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.min

fun main() {
    val input = File("input/day16.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private data class Valve(
    val name: String,
    val rate: Int,
    val tunnels: List<String>
)

private fun puzzleOne(input: List<String>): Any {
    val valves = parseValves(input)
    val distances = calculateDistances(valves)

    val start = valves.indices.find { valves[it].name == "AA" }!!
    val pressurized = valves.indices.filter { valves[it].rate != 0 }.toSet()

    fun releaseMax(i: Int, remaining: Set<Int>, time: Int, released: Int): Int {
        if (time == 0 || remaining.isEmpty()) {
            return released
        }
        return remaining.asSequence()
            .filter { j -> distances[i][j] + 1 < time }
            .maxOfOrNull { j ->
                val remainTime = time - distances[i][j] - 1
                val releasedFromJ = valves[j].rate * remainTime
                releaseMax(j, remaining - j, remainTime, released + releasedFromJ)
            } ?: released
    }

    return releaseMax(start, pressurized, 30, 0)
}

private fun puzzleTwo(input: List<String>): Any {
    val valves = parseValves(input)
    val distances = calculateDistances(valves)

    val start = valves.indices.find { valves[it].name == "AA" }!!
    val pressurized = valves.indices.filter { valves[it].rate != 0 }

    fun releaseMax(i: Int, remaining: Set<Int>, time: Int, released: Int): Int {
        if (time == 0 || remaining.isEmpty()) {
            return released
        }
        return remaining.asSequence()
            .filter { j -> distances[i][j] + 1 < time }
            .maxOfOrNull { j ->
                val remainTime = time - distances[i][j] - 1
                val releasedFromJ = valves[j].rate * remainTime
                releaseMax(j, remaining - j, remainTime, released + releasedFromJ)
            } ?: released
    }

    var max = 0
    val maxMask = (1 shl (pressurized.size - 1)) - 1
    for (mask in 0..maxMask) {
        val humanValves = pressurized.filterIndexed { i, _ -> mask and (1 shl i) > 0 }.toSet()
        val elephantValves = pressurized.filterIndexed { i, _ -> mask and (1 shl i) == 0 }.toSet()
        val releaseMax = releaseMax(start, humanValves, 26, 0) + releaseMax(start, elephantValves, 26, 0)
        max = max(max, releaseMax)
    }

    return max
}

private fun calculateDistances(valves: List<Valve>): Array<IntArray> {
    val distances = Array(valves.size) { i ->
        IntArray(valves.size) { j ->
            if (i == j) 0 else
                if (valves[i].tunnels.contains(valves[j].name)) 1 else 1000
        }
    }

    for (k in valves.indices) {
        for (i in valves.indices) {
            for (j in valves.indices) {
                distances[i][j] = min(distances[i][k] + distances[k][j], distances[i][j])
            }
        }
    }
    return distances
}

private fun parseValves(input: List<String>): List<Valve> {
    val pattern =
        Pattern.compile("""Valve ([A-Z]{2}) has flow rate=(\d+); tunnels? leads? to valves? ((?:(?:, )?[A-Z]{2})+)""")

    val valves = input.map { line ->
        val matcher = pattern.matcher(line)
        matcher.matches()
        val name = matcher.group(1)
        val rate = matcher.group(2).toInt()
        val tunnels = matcher.group(3).split(", ")
        Valve(name, rate, tunnels)
    }
    return valves
}
