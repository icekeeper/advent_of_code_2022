import java.io.File

fun main() {
    val input = File("input/day03.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private fun puzzleOne(input: List<String>): Any {
    return input.sumOf { line ->
        val first = line.substring(0, line.length / 2).toCharArray().toSet()
        val second = line.substring(line.length / 2).toCharArray().toSet()
        when (val char = (first intersect second).first()) {
            in 'a'..'z' -> char - 'a' + 1
            else -> char - 'A' + 27
        }
    }
}

private fun puzzleTwo(input: List<String>): Any {
    return input.chunked(3).sumOf { chunk ->
        val char = chunk.map { it.toCharArray().toSet() }
            .reduce(Set<Char>::intersect)
            .first()
        when (char) {
            in 'a'..'z' -> char - 'a' + 1
            else -> char - 'A' + 27
        }
    }
}