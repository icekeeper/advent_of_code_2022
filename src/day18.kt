import java.io.File
import kotlin.math.abs

fun main() {
    val input = File("input/day18.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private data class Cube(val x: Int, val y: Int, val z: Int) {
    fun isAdjacent(c: Cube) =
        abs(x - c.x) + abs(y - c.y) + abs(z - c.z) == 1

    fun allAdjacent() =
        listOf(
            copy(x = x - 1), copy(y = y - 1), copy(z = z - 1),
            copy(x = x + 1), copy(y = y + 1), copy(z = z + 1)
        )

}

private fun puzzleOne(input: List<String>): Any {
    val cubes = parseCubes(input)

    val adjacent = cubes.indices.sumOf { i ->
        (i + 1..cubes.lastIndex).count { j -> cubes[i].isAdjacent(cubes[j]) }
    }

    return cubes.size * 6 - adjacent * 2
}

private fun puzzleTwo(input: List<String>): Any {
    val cubes = parseCubes(input).toSet()

    val cache = Axis.values().associateWith { makeCache(it, cubes) }

    val airCubes = mutableSetOf<Cube>()
    val waterCubes = mutableSetOf<Cube>()

    fun Cube.anyAxisFree() = Axis.values().any { axis ->
        Direction.values().any { direction ->
            cache.getValue(axis).noMoreCubes(this, direction)
        }
    }

    fun isWater(c: Cube): Boolean {
        if (waterCubes.contains(c)) {
            return true
        } else if (airCubes.contains(c) || cubes.contains(c)) {
            return false
        } else {
            val linked = mutableSetOf<Cube>()
            val queue = ArrayDeque<Cube>()
            queue.addLast(c)
            linked.add(c)

            while (!queue.isEmpty()) {
                val cc = queue.removeFirst()
                if (waterCubes.contains(cc) || cc.anyAxisFree()) {
                    waterCubes.addAll(linked)
                    return true
                }
                cc.allAdjacent().forEach { ac ->
                    if (!cubes.contains(ac) && !linked.contains(ac)) {
                        queue.addLast(ac)
                        linked.add(ac)
                    }
                }
            }
            airCubes.addAll(linked)
            return false
        }
    }

    return cubes.sumOf { it.allAdjacent().count(::isWater) }
}


private fun parseCubes(input: List<String>): List<Cube> = input.map {
    val (x, y, z) = it.split(',').map(String::toInt)
    Cube(x, y, z)
}


private enum class Axis(
    val c: (Cube) -> Int,
    val c1: (Cube) -> Int,
    val c2: (Cube) -> Int
) {
    X(Cube::x, Cube::y, Cube::z),
    Y(Cube::y, Cube::z, Cube::x),
    Z(Cube::z, Cube::x, Cube::y),
}

private enum class Direction(val sign: Int) { ALONG(1), AGAINST(-1) }

private data class AxisCache(
    val axis: Axis,
    val cache: Map<Int, Map<Int, List<Cube>>>
) {
    fun noMoreCubes(c: Cube, direction: Direction): Boolean {
        return cache[axis.c1(c)]?.get(axis.c2(c))?.all {
            sign(axis.c(it) - axis.c(c)) != direction.sign
        } ?: true
    }
}

private fun sign(i: Int) = when {
    i > 0 -> 1
    i < 0 -> -1
    else -> 0
}

private fun makeCache(axis: Axis, cubes: Collection<Cube>): AxisCache {
    val map = mutableMapOf<Int, MutableMap<Int, MutableList<Cube>>>()
    cubes.forEach { cube ->
        map.getOrPut(axis.c1(cube)) { mutableMapOf() }
            .getOrPut(axis.c2(cube)) { mutableListOf() }
            .add(cube)
    }
    return AxisCache(axis, map)
}