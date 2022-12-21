import java.io.File
import kotlin.math.abs

fun main() {
    val input = File("input/day21.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

sealed interface MathMonkey {
    val name: String
    fun shout(monkeys: Map<String, MathMonkey>): Double
}

data class ConstantMonkey(override val name: String, val number: Double) : MathMonkey {
    override fun shout(monkeys: Map<String, MathMonkey>) = number
}

class OperationMonkey(
    override val name: String,
    private val name1: String,
    private val name2: String,
    private val op: (Double, Double) -> Double
) : MathMonkey {

    private var number: Double? = null

    override fun shout(monkeys: Map<String, MathMonkey>): Double {
        if (number == null) {
            val n1 = monkeys.getValue(name1).shout(monkeys)
            val n2 = monkeys.getValue(name2).shout(monkeys)
            number = op(n1, n2)
        }
        return number!!
    }

    fun reset() {
        number = null
    }
}

class MathHuman(var number: Double) : MathMonkey {
    override val name: String
        get() = "humn"

    override fun shout(monkeys: Map<String, MathMonkey>): Double = number
}

class RootMonkey(
    private val name1: String,
    private val name2: String
) : MathMonkey {
    override val name: String
        get() = "root"

    override fun shout(monkeys: Map<String, MathMonkey>): Double {
        val n1 = monkeys.getValue(name1).shout(monkeys)
        val n2 = monkeys.getValue(name2).shout(monkeys)
        return n2 - n1
    }
}

private fun puzzleOne(input: List<String>): Any? {
    fun parseMonkey(s: String): MathMonkey {
        val tokens = s.split(' ')
        return if (tokens.size == 2) {
            val (name, number) = tokens
            ConstantMonkey(name.dropLast(1), number.toDouble())
        } else {
            val (name, op1, op, op2) = tokens
            val operation: (Double, Double) -> Double = when (op) {
                "+" -> Double::plus
                "-" -> Double::minus
                "*" -> Double::times
                else -> Double::div
            }
            OperationMonkey(name.dropLast(1), op1, op2, operation)
        }
    }

    val monkeys = input.map(::parseMonkey).associateBy { it.name }
    return monkeys["root"]?.shout(monkeys)?.toLong()
}

private fun puzzleTwo(input: List<String>): Any {
    fun parseMonkey(s: String): MathMonkey {
        val tokens = s.split(' ')
        val name = tokens.first().dropLast(1)
        return if (name == "root") {
            val (_, op1, _, op2) = tokens
            RootMonkey(op1, op2)
        } else if (name == "humn") {
            MathHuman(0.0)
        } else if (tokens.size == 2) {
            val (_, number) = tokens
            ConstantMonkey(name, number.toDouble())
        } else {
            val (_, op1, op, op2) = tokens
            val operation: (Double, Double) -> Double = when (op) {
                "+" -> Double::plus
                "-" -> Double::minus
                "*" -> Double::times
                else -> Double::div
            }
            OperationMonkey(name, op1, op2, operation)
        }
    }

    val monkeys = input.map(::parseMonkey).associateBy { it.name }
    val rootMonkey = monkeys["root"] as RootMonkey
    val human = monkeys["humn"] as MathHuman

    var min = 0.0
    var max = 10000000000000.0

    fun check(n: Double): Double {
        human.number = n
        val result = rootMonkey.shout(monkeys)
        monkeys.values.filterIsInstance<OperationMonkey>().forEach { it.reset() }
        return result
    }

    var guess = max / 2.0

    var result = check(guess)
    while (abs(result) > 0.000000001) {
        if (abs(min - max) < 0.000000001) return "not found"
        if (result > 0) {
            max = guess
        } else {
            min = guess
        }
        guess = (min + max) / 2
        result = check(guess)
    }

    return guess.toLong()
}