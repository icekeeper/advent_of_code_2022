import java.io.File

fun main() {
    val input = File("input/day11.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

data class Monkey(
    val number: Int,
    val startingItems: List<Int>,
    val divider: Int,
    val inspect: (level: Int) -> Int,
    val test: (level: Int) -> Int,
)

private fun parseMonkeys(input: List<String>): List<Monkey> {
    return input.asSequence().chunked(7) { lines ->
        val number = lines[0].substring("Monkey ".length, lines[0].length - 1).toInt()
        val items = lines[1].substring("  Starting items: ".length)
            .split(',').map { it.trim().toInt() }

        val (op, operand) = lines[2].substring("  Operation: new = old ".length).split(' ')
        val operation = when (operand) {
            "old" -> if (op == "*") { l: Int -> l * l } else { l: Int -> l + l }
            else -> {
                val const = operand.toInt()
                if (op == "*") { l: Int -> l * const } else { l: Int -> l + const }
            }
        }

        val divider = lines[3].substring("  Test: divisible by ".length).toInt()
        val trueReceiver = lines[4].substring("    If true: throw to monkey ".length).toInt()
        val falseReceiver = lines[5].substring("    If false: throw to monkey ".length).toInt()
        val test = { l: Int -> if (l % divider == 0) trueReceiver else falseReceiver }

        Monkey(number, items, divider, operation, test)
    }.toList()
}

private fun puzzleOne(input: List<String>): Any {
    val monkeys = parseMonkeys(input)
    val stat = Array(monkeys.size) { 0 }
    val items = mutableMapOf<Int, MutableList<Int>>()
    monkeys.forEach { monkey ->
        items[monkey.number] = monkey.startingItems.toMutableList()
    }

    repeat(20) {
        monkeys.forEach { monkey ->
            items.getValue(monkey.number).forEach { item ->
                stat[monkey.number]++
                val inspectedItem = monkey.inspect(item) / 3
                items.getValue(monkey.test(inspectedItem)).add(inspectedItem)
            }
            items[monkey.number] = mutableListOf()
        }
    }

    return stat.asSequence().sortedDescending().take(2).reduce(Int::times)
}

private fun puzzleTwo(input: List<String>): Any {
    val monkeys = parseMonkeys(input)

    data class Item(val reminders: List<Int>)

    fun Monkey.inspect(item: Item) = Item(
        item.reminders.mapIndexed { index, reminder -> this.inspect(reminder) % monkeys[index].divider }
    )

    fun Monkey.test(item: Item): Int = this.test(item.reminders[this.number])

    val stat = Array(monkeys.size) { 0L }
    val items = mutableMapOf<Int, MutableList<Item>>()
    monkeys.forEach { monkey ->
        items[monkey.number] = monkey.startingItems.map { level ->
            Item(monkeys.map { level % it.divider })
        }.toMutableList()
    }


    repeat(10000) {
        monkeys.forEach { monkey ->
            items.getValue(monkey.number).forEach { item ->
                stat[monkey.number]++
                val inspectedItem = monkey.inspect(item)
                items.getValue(monkey.test(inspectedItem)).add(inspectedItem)
            }
            items[monkey.number] = mutableListOf()
        }
    }

    return stat.asSequence().sortedDescending().take(2).reduce(Long::times)
}