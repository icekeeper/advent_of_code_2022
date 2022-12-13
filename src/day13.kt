import java.io.File
import java.util.*

fun main() {
    val input = File("input/day13.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private sealed interface Packet

private data class Number(val value: Int) : Packet

private data class PacketList(val value: List<Packet>) : Packet

private fun parsePacket(s: String): Packet {
    val tokenizer = StringTokenizer(s, "[],", true)
    val tokens = mutableListOf<String>()
    while (tokenizer.hasMoreTokens()) {
        val token = tokenizer.nextToken()
        if (token != ",") {
            tokens.add(token)
        }
    }

    var pos = 0

    fun parseNext(): Packet {
        return if (tokens[pos] == "[") {
            val packets = mutableListOf<Packet>()
            pos++
            while (tokens[pos] != "]") {
                packets.add(parseNext())
            }
            pos++
            PacketList(packets)
        } else {
            Number(tokens[pos++].toInt())
        }
    }

    return parseNext()
}

private fun compare(p1: Packet, p2: Packet): Int {
    return when (p1) {
        is Number -> when (p2) {
            is Number -> when {
                p1.value < p2.value -> -1
                p1.value > p2.value -> 1
                else -> 0
            }

            is PacketList -> compare(PacketList(listOf(p1)), p2)
        }

        is PacketList -> when (p2) {
            is Number -> compare(p1, PacketList(listOf(p2)))

            is PacketList -> {
                var i = 0
                var j = 0

                while (i in p1.value.indices && j in p2.value.indices) {
                    val check = compare(p1.value[i++], p2.value[j++])
                    if (check != 0) return check
                }
                if (i !in p1.value.indices && j !in p2.value.indices) {
                    return 0
                } else if (i !in p1.value.indices) {
                    return -1
                } else {
                    return 1
                }
            }
        }
    }
}

private fun puzzleOne(input: List<String>): Any {
    return input.chunked(3).mapIndexed { index, chunk ->
        val p1 = parsePacket(chunk[0])
        val p2 = parsePacket(chunk[1])
        if (compare(p1, p2) == -1) index + 1 else 0
    }.sum()
}

private fun puzzleTwo(input: List<String>): Any {
    val divider1 = PacketList(listOf(PacketList(listOf(Number(2)))))
    val divider2 = PacketList(listOf(PacketList(listOf(Number(6)))))
    return input.asSequence()
        .filter { it.isNotBlank() }
        .map { parsePacket(it) }
        .plus(divider1)
        .plus(divider2)
        .sortedWith(::compare)
        .mapIndexed { index, packet ->
            if (packet == divider1 || packet == divider2) index + 1 else 1
        }
        .reduce(Int::times)
}