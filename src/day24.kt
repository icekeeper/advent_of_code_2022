import java.io.File

fun main() {
    val input = File("input/day24.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private enum class Blizzard {
    UP, DOWN, LEFT, RIGHT
}

private fun puzzleOne(input: List<String>): Any {
    val blizzards = parseBlizzards(input)

    val my = blizzards.size
    val mx = blizzards.first().size

    val cycle = lcm(my, mx)

    println("my: $my mx: $mx cycle: $cycle")

    fun hasBlizzard(x: Int, y: Int, m: Int) =
        blizzards[(y - (m % my) + my) % my][x] == Blizzard.DOWN
                || blizzards[(y + m) % my][x] == Blizzard.UP
                || blizzards[y][(x - (m % mx) + mx) % mx] == Blizzard.RIGHT
                || blizzards[y][(x + m) % mx] == Blizzard.LEFT

    data class Position(val x: Int, val y: Int, val m: Int) {
        fun next() = sequence {
            val nm = (m + 1) % cycle
            if (y == -1 || y == my || !hasBlizzard(x, y, nm))
                yield(Position(x, y, nm))
            if ((x == 0 && y == 0) || (y > 0 && !hasBlizzard(x, y - 1, nm)))
                yield(Position(x, y - 1, nm))
            if ((x == mx - 1 && y == my - 1) || (y < my - 1 && !hasBlizzard(x, y + 1, nm)))
                yield(Position(x, y + 1, nm))
            if (x > 0 && y in 0 until my && !hasBlizzard(x - 1, y, nm))
                yield(Position(x - 1, y, nm))
            if (x < mx - 1 && y in 0 until my && !hasBlizzard(x + 1, y, nm))
                yield(Position(x + 1, y, nm))
        }
    }


    val queue = ArrayDeque<Pair<Position, Int>>()
    val starting = Position(0, -1, 0)
    val visited = mutableSetOf(starting)
    queue.addLast(Pair(starting, 0))
    while (queue.isNotEmpty()) {
        val (p, d) = queue.removeFirst()
        if (p.x == mx - 1 && p.y == my) {
            return d
        }
        p.next().forEach { next ->
            if (visited.add(next)) {
                queue.addLast(Pair(next, d + 1))
            }
        }
    }

    return -1
}

private fun puzzleTwo(input: List<String>): Any {
    val blizzards = parseBlizzards(input)

    val my = blizzards.size
    val mx = blizzards.first().size

    val cycle = lcm(my, mx)

    println("my: $my mx: $mx cycle: $cycle")

    fun hasBlizzard(x: Int, y: Int, m: Int) =
        blizzards[(y - (m % my) + my) % my][x] == Blizzard.DOWN
                || blizzards[(y + m) % my][x] == Blizzard.UP
                || blizzards[y][(x - (m % mx) + mx) % mx] == Blizzard.RIGHT
                || blizzards[y][(x + m) % mx] == Blizzard.LEFT

    data class Position(val x: Int, val y: Int, val m: Int) {
        fun next() = sequence {
            val nm = (m + 1) % cycle
            if (y == -1 || y == my || !hasBlizzard(x, y, nm))
                yield(Position(x, y, nm))
            if ((x == 0 && y == 0) || (y > 0 && !hasBlizzard(x, y - 1, nm)))
                yield(Position(x, y - 1, nm))
            if ((x == mx - 1 && y == my - 1) || (y < my - 1 && !hasBlizzard(x, y + 1, nm)))
                yield(Position(x, y + 1, nm))
            if (x > 0 && y in 0 until my && !hasBlizzard(x - 1, y, nm))
                yield(Position(x - 1, y, nm))
            if (x < mx - 1 && y in 0 until my && !hasBlizzard(x + 1, y, nm))
                yield(Position(x + 1, y, nm))
        }
    }


    fun shortestPath(s: Position, tx: Int, ty: Int): Pair<Position, Int>? {
        val queue = ArrayDeque<Pair<Position, Int>>()
        val visited = mutableSetOf(s)
        queue.addLast(Pair(s, 0))
        while (queue.isNotEmpty()) {
            val (p, d) = queue.removeFirst()
            if (p.x == tx && p.y == ty) {
                return Pair(p, d)
            }
            p.next().forEach { next ->
                if (visited.add(next)) {
                    queue.addLast(Pair(next, d + 1))
                }
            }
        }

        return null
    }

    val firstPath = shortestPath(Position(0, -1, 0), mx - 1, my)
    println(firstPath)
    val secondPath = shortestPath(firstPath!!.first, 0, -1)
    println(secondPath)
    val thirdPath = shortestPath(secondPath!!.first, mx - 1, my)
    println(thirdPath)

    return firstPath.second + secondPath.second + thirdPath!!.second
}

private fun parseBlizzards(input: List<String>): List<List<Blizzard?>> {
    val blizzards = input.subList(1, input.size - 1).map { line ->
        line.subSequence(1, line.length - 1).map {
            when (it) {
                '^' -> Blizzard.UP
                '<' -> Blizzard.LEFT
                '>' -> Blizzard.RIGHT
                'v' -> Blizzard.DOWN
                else -> null
            }
        }
    }
    return blizzards
}

private fun lcm(a: Int, b: Int) = a / gcd(a, b) * b

private fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
