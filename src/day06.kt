import java.io.File

fun main() {
    val input = File("input/day06.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private fun puzzleOne(input: List<String>): Any {
    return input.map { detectPacket(it) }.joinToString()
}

fun detectPacket(s: String): Int {
    return s.asSequence().windowed(4).indexOfFirst { it.toSet().size == 4 } + 4
}

private fun puzzleTwo(input: List<String>): Any {
    return input.map { detectMessage(it) }.joinToString()
}

fun detectMessage(s: String): Int {
    return s.asSequence().windowed(14).indexOfFirst { it.toSet().size == 14 } + 14
}