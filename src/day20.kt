import java.io.File

fun main() {
    val input = File("input/day20.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private fun puzzleOne(input: List<String>): Any {
    val numbers = input.map { it.toInt() }
    val inputPositionToPosition = IntArray(numbers.size) { it }
    val positionToInputPosition = IntArray(numbers.size) { it }

    fun next(p: Int) = (p + 1) % numbers.size
    fun prev(p: Int) = (p + numbers.size - 1) % numbers.size

    fun swap(p1: Int, p2: Int) {
        val ip1 = positionToInputPosition[p1]
        val ip2 = positionToInputPosition[p2]

        positionToInputPosition[p1] = ip2
        positionToInputPosition[p2] = ip1

        inputPositionToPosition[ip1] = p2
        inputPositionToPosition[ip2] = p1
    }

    fun moveRight(ip: Int) = swap(inputPositionToPosition[ip], next(inputPositionToPosition[ip]))
    fun moveLeft(ip: Int) = swap(inputPositionToPosition[ip], prev(inputPositionToPosition[ip]))

    fun move(ip: Int) {
        if (numbers[ip] > 0) {
            repeat(numbers[ip]) { moveRight(ip) }
        } else {
            repeat(-numbers[ip]) { moveLeft(ip) }
        }
    }

    numbers.indices.forEach(::move)

    val zeroIndex = inputPositionToPosition[numbers.indexOf(0)]
    println("Zero index $zeroIndex")

    return sequenceOf(1000, 2000, 3000).sumOf {
        println("${it}th number after zero is ${numbers[positionToInputPosition[(zeroIndex + it) % numbers.size]]}")
        numbers[positionToInputPosition[(zeroIndex + it) % numbers.size]]
    }
}

private fun puzzleTwo(input: List<String>): Any {
    val numbers = input.map { it.toLong() * 811589153 }
    val inputPositionToPosition = IntArray(numbers.size) { it }
    val positionToInputPosition = IntArray(numbers.size) { it }

    fun next(p: Int) = (p + 1) % numbers.size
    fun prev(p: Int) = (p + numbers.size - 1) % numbers.size

    fun swap(p1: Int, p2: Int) {
        val ip1 = positionToInputPosition[p1]
        val ip2 = positionToInputPosition[p2]

        positionToInputPosition[p1] = ip2
        positionToInputPosition[p2] = ip1

        inputPositionToPosition[ip1] = p2
        inputPositionToPosition[ip2] = p1
    }

    fun moveRight(ip: Int) = swap(inputPositionToPosition[ip], next(inputPositionToPosition[ip]))
    fun moveLeft(ip: Int) = swap(inputPositionToPosition[ip], prev(inputPositionToPosition[ip]))

    fun move(ip: Int) {
        if (numbers[ip] > 0) {
            repeat((numbers[ip] % (numbers.size - 1)).toInt()) { moveRight(ip) }
        } else {
            repeat((-numbers[ip] % (numbers.size - 1)).toInt()) { moveLeft(ip) }
        }
    }

    repeat(10) { numbers.indices.forEach(::move) }

    val zeroIndex = inputPositionToPosition[numbers.indexOf(0)]
    println("Zero index $zeroIndex")

    return sequenceOf(1000, 2000, 3000).sumOf {
        println("${it}th number after zero is ${numbers[positionToInputPosition[(zeroIndex + it) % numbers.size]]}")
        numbers[positionToInputPosition[(zeroIndex + it) % numbers.size]]
    }
}