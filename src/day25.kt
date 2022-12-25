import java.io.File

fun main() {
    val input = File("input/day25.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private fun puzzleOne(input: List<String>): Any {
    return input.sumOf { it.fromSnafuToLong() }.toSnafu()
}

private fun puzzleTwo(input: List<String>): Any {
    return "Yay!"
}

private fun Long.toSnafu(): String {
    var num = this
    return sequence {
        while (num > 0) {
            val rem = num % 5
            when (rem) {
                0L -> yield('0')
                1L -> yield('1')
                2L -> yield('2')
                3L -> yield('=')
                4L -> yield('-')
            }
            num = (num / 5) + if (rem < 3) 0 else 1
        }
    }.joinToString(separator = "").reversed()
}

private fun String.fromSnafuToLong(): Long =
    this.reversed().foldRightIndexed(0) { i, c, acc ->
        acc + 5L.pow(i) * when (c) {
            '2' -> 2
            '1' -> 1
            '0' -> 0
            '-' -> -1
            '=' -> -2
            else -> throw RuntimeException("Invalid character $c")
        }
    }

private fun Long.pow(i: Int): Long =
    generateSequence { this }.take(i).fold(1, Long::times)