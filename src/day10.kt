import java.io.File
import kotlin.math.abs

fun main() {
    val input = File("input/day10K.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private fun puzzleOne(input: List<String>): Any {
    var x = 1
    var cycle = 1

    var sum = 0

    fun incCycle() {
        if ((cycle - 20) % 40 == 0) {
            sum += x * cycle
        }
        cycle++
    }

    input.forEach { cmd ->
        when {
            cmd.startsWith("noop") -> incCycle()
            cmd.startsWith("addx") -> {
                incCycle()
                incCycle()
                x += cmd.substring(5).toInt()
            }
        }
    }

    incCycle()
    return sum
}

private fun puzzleTwo(input: List<String>): Any {
    var x = 1
    var cycle = 0

    val display = Array(6) { Array(40) { '.' } }

    fun incCycle() {
        val column = cycle % 40
        if (abs(x - column) < 2) {
            display[cycle / 40][column] = '#'
        }
        cycle++
    }

    input.forEach { cmd ->
        when {
            cmd.startsWith("noop") -> incCycle()
            cmd.startsWith("addx") -> {
                incCycle()
                incCycle()
                x += cmd.substring(5).toInt()
            }
        }
    }

    incCycle()
    return display.joinToString(separator = "\n") { it.joinToString(separator = "") }
}