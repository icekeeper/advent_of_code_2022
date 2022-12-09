import java.io.File

fun main() {
    val input = File("input/day05.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private fun puzzleOne(input: List<String>): Any {
    val stacks = parseStacks(input)

    val commands = input.takeLastWhile { it.isNotEmpty() }
    commands.forEach { command ->
        val (count, from, to) = command.split("move", "from", "to").filter { it.isNotBlank() }.map { it.trim().toInt() }
        repeat(count) {
            stacks.getValue(to).addFirst(stacks.getValue(from).removeFirst())
        }
    }

    return stacks.entries.sortedBy { it.key }.map { it.value.first() }.joinToString(separator = "")
}

private fun parseStacks(input: List<String>): MutableMap<Int, ArrayDeque<Char>> {
    val stacks = mutableMapOf<Int, ArrayDeque<Char>>()
    val stacksInput = input.takeWhile { it.isNotEmpty() }
    stacksInput.last().forEachIndexed { i, c ->
        if (c.isDigit()) {
            val stack = ArrayDeque<Char>()
            stacksInput.forEach {
                if (i < it.length && it[i].isLetter()) stack.addLast(it[i])
            }
            stacks[c.digitToInt()] = stack
        }
    }
    return stacks
}

private fun puzzleTwo(input: List<String>): Any {
    val stacks = parseStacks(input)

    val commands = input.takeLastWhile { it.isNotEmpty() }
    commands.forEach { command ->
        val (count, from, to) = command.split("move", "from", "to").filter { it.isNotBlank() }.map { it.trim().toInt() }
        val stackFrom = stacks.getValue(from)
        val stackTo = stacks.getValue(to)
        stackFrom.take(count).reversed().forEach(stackTo::addFirst)
        repeat(count) { stackFrom.removeFirst() }
    }

    return stacks.entries.sortedBy { it.key }.map { it.value.first() }.joinToString(separator = "")
}