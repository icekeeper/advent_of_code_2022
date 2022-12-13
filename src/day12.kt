import java.io.File
import java.lang.RuntimeException

fun main() {
    val input = File("input/day12.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private fun puzzleOne(input: List<String>): Any {
    val map = input.map { it.toCharArray() }
    val steps = Array(map.size) { Array(map[it].size) { Integer.MAX_VALUE } }
    fun findCharacter(c: Char): Pair<Int, Int> {
        map.forEachIndexed { i, s ->
            s.forEachIndexed { j, char ->
                if (char == c) return Pair(i, j)
            }
        }
        throw RuntimeException()
    }

    val start = findCharacter('S')
    val finish = findCharacter('E')

    map[start.first][start.second] = 'a'
    map[finish.first][finish.second] = 'z'
    steps[start.first][start.second] = 0

    val queue = ArrayDeque<Pair<Int, Int>>()
    queue.addFirst(start)

    while (queue.isNotEmpty()) {
        val p = queue.removeFirst()

        fun check(n: Pair<Int, Int>) {
            if (map[n.first][n.second] - map[p.first][p.second] <= 1
                && steps[p.first][p.second] + 1 < steps[n.first][n.second]
            ) {
                steps[n.first][n.second] = steps[p.first][p.second] + 1
                queue.addLast(n)
            }
        }

        if (p.first > 0) check(p.copy(first = p.first - 1))
        if (p.first < steps.lastIndex) check(p.copy(first = p.first + 1))
        if (p.second > 0) check(p.copy(second = p.second - 1))
        if (p.second < steps[p.first].lastIndex) check(p.copy(second = p.second + 1))
    }

    return steps[finish.first][finish.second]
}

private fun puzzleTwo(input: List<String>): Any {
    val map = input.map { it.toCharArray() }
    val steps = Array(map.size) { Array(map[it].size) { Integer.MAX_VALUE } }
    fun findCharacter(c: Char): Pair<Int, Int> {
        map.forEachIndexed { i, s ->
            s.forEachIndexed { j, char ->
                if (char == c) return Pair(i, j)
            }
        }
        throw RuntimeException()
    }

    val start = findCharacter('E')
    val finish = findCharacter('S')

    map[start.first][start.second] = 'z'
    map[finish.first][finish.second] = 'a'
    steps[start.first][start.second] = 0

    val queue = ArrayDeque<Pair<Int, Int>>()
    queue.addFirst(start)

    while (queue.isNotEmpty()) {
        val p = queue.removeFirst()

        fun check(n: Pair<Int, Int>) {
            if (map[p.first][p.second] - map[n.first][n.second] <= 1
                && steps[p.first][p.second] + 1 < steps[n.first][n.second]
            ) {
                steps[n.first][n.second] = steps[p.first][p.second] + 1
                queue.addLast(n)
            }
        }

        if (p.first > 0) check(p.copy(first = p.first - 1))
        if (p.first < steps.lastIndex) check(p.copy(first = p.first + 1))
        if (p.second > 0) check(p.copy(second = p.second - 1))
        if (p.second < steps[p.first].lastIndex) check(p.copy(second = p.second + 1))
    }

    var min = steps[finish.first][finish.second]
    for (i in map.indices) {
        for (j in map[i].indices) {
            if (map[i][j] == 'a' && min > steps[i][j])
                min = steps[i][j]
        }
    }

    return min
}