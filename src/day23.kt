import java.io.File

fun main() {
    val input = File("input/day23.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private enum class ElfDirection {
    N, S, W, E;

    fun next() = ElfDirection.values()[(this.ordinal + 1) % 4]
}

private data class Elf(val x: Int, val y: Int) {
    fun moveTo(d: ElfDirection) = when (d) {
        ElfDirection.N -> Elf(x, y - 1)
        ElfDirection.S -> Elf(x, y + 1)
        ElfDirection.W -> Elf(x - 1, y)
        ElfDirection.E -> Elf(x + 1, y)
    }
}

private class ElfMap {
    val map = mutableMapOf<Int, MutableSet<Int>>()

    fun addElf(x: Int, y: Int) {
        map.getOrPut(x) { mutableSetOf() }.add(y)
    }

    fun removeElf(fx: Int, fy: Int) {
        map.compute(fx) { _, set ->
            set?.remove(fy)
            if (set.isNullOrEmpty()) null else set
        }
    }

    fun isOccupied(x: Int, y: Int) = map[x]?.contains(y) ?: false

    fun elfs() = sequence {
        map.forEach { (x, line) ->
            line.forEach { y ->
                yield(Elf(x, y))
            }
        }
    }

    fun isMoving(e: Elf) = (e.x - 1..e.x + 1).sumOf { x ->
        (e.y - 1..e.y + 1).count { y -> isOccupied(x, y) }
    } > 1

    fun isDirectionFree(x: Int, y: Int, d: ElfDirection): Boolean = when (d) {
        ElfDirection.N -> (x - 1..x + 1).none { isOccupied(it, y - 1) }
        ElfDirection.S -> (x - 1..x + 1).none { isOccupied(it, y + 1) }
        ElfDirection.W -> (y - 1..y + 1).none { isOccupied(x - 1, it) }
        ElfDirection.E -> (y - 1..y + 1).none { isOccupied(x + 1, it) }
    }

    fun getMinRectangle(): IntArray {
        val result = IntArray(4)
        result[0] = map.keys.min()
        result[1] = map.values.minOf { it.min() }
        result[2] = map.keys.max()
        result[3] = map.values.maxOf { it.max() }
        return result
    }

    fun countEmptySpots(): Int {
        val rect = getMinRectangle()
        return (rect[0]..rect[2]).sumOf { x ->
            (rect[1]..rect[3]).count { y ->
                !isOccupied(x, y)
            }
        }
    }

    fun print() {
        val rect = getMinRectangle()
        (rect[1]..rect[3]).forEach { y ->
            (rect[0]..rect[2]).forEach { x ->
                if (isOccupied(x, y)) {
                    print('#')
                } else {
                    print('.')
                }
            }
            println()
        }
    }
}

private fun puzzleOne(input: List<String>): Any {
    val map = ElfMap()
    input.forEachIndexed { y, s ->
        s.forEachIndexed { x, c ->
            if (c == '#') map.addElf(x, y)
        }
    }
    var sd = ElfDirection.N
    repeat(10) {
        val proposal = mutableMapOf<Elf, MutableList<Elf>>()
        map.elfs().filter { map.isMoving(it) }.forEach { e ->
            var d = sd
            do {
                if (map.isDirectionFree(e.x, e.y, d)) {
                    proposal.getOrPut(e.moveTo(d)) { mutableListOf() }.add(e)
                    break
                }
                d = d.next()
            } while (d != sd)
        }
        proposal.filter { it.value.size == 1 }.forEach { (k, v) ->
            val (e) = v
            map.removeElf(e.x, e.y)
            map.addElf(k.x, k.y)
        }
        sd = sd.next()
    }

    return map.countEmptySpots()
}

private fun puzzleTwo(input: List<String>): Any {
    val map = ElfMap()
    input.forEachIndexed { y, s ->
        s.forEachIndexed { x, c ->
            if (c == '#') map.addElf(x, y)
        }
    }
    var sd = ElfDirection.N
    var rounds = 0
    do {
        rounds += 1
        val proposal = mutableMapOf<Elf, MutableList<Elf>>()
        map.elfs().filter { map.isMoving(it) }.forEach { e ->
            var d = sd
            do {
                if (map.isDirectionFree(e.x, e.y, d)) {
                    proposal.getOrPut(e.moveTo(d)) { mutableListOf() }.add(e)
                    break
                }
                d = d.next()
            } while (d != sd)
        }
        proposal.filter { it.value.size == 1 }.forEach { (k, v) ->
            val (e) = v
            map.removeElf(e.x, e.y)
            map.addElf(k.x, k.y)
        }
        sd = sd.next()
    } while (proposal.any { it.value.size == 1 })

    return rounds
}