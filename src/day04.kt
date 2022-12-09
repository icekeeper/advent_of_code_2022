import java.io.File

fun main() {
    val input = File("input/day04.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

typealias Field = Pair<Int, Int>

fun toField(s: String): Field = s.split('-').map { it.toInt() }.zipWithNext().first()

infix fun Field.contains(f: Field) = this.first <= f.first && f.second <= this.second
infix fun Field.overlap(f: Field) = this.first <= f.first && f.first <= this.second
        || this.first <= f.second && f.second <= this.second

private fun puzzleOne(input: List<String>): Any {
    return input.count { line ->
        val pair = line.split(',').map(::toField)
        pair[0] contains pair[1] || pair[1] contains pair[0]
    }
}

private fun puzzleTwo(input: List<String>): Any {
    return input.count { line ->
        val pair = line.split(',').map(::toField)
        pair[0] overlap pair[1] || pair[1] overlap pair[0]
    }
}