import java.io.File
import java.lang.RuntimeException
import kotlin.math.max

fun main() {
    val input = File("input/day17.txt").readLines()

    val rocks = listOf(
        """
        ####
    """.trimIndent(),
        """
       .#.
       ###
       .#.
    """.trimIndent(),
        """
        ..#
        ..#
        ###
    """.trimIndent(),
        """
        #
        #
        #
        #
    """.trimIndent(),
        """
        ##
        ##
    """.trimIndent()
    ).map { s ->
        s.split('\n')
            .map { it.map { c -> c == '#' }.toBooleanArray() }
            .reversed()
            .toTypedArray()
    }

    data class Block(val rock: Int, var lx: Int, var by: Int) {
        val rx: Int
            get() = lx + rocks[rock][0].size - 1
        val ty: Int
            get() = by + rocks[rock].size - 1

        fun isOccupied(x: Int, y: Int) =
            x in lx..rx && y in by..ty && rocks[rock][y - by][x - lx]

        fun collidesWithWall() = lx < 0 || by < 0 || rx > 6
    }

    fun isColliding(b1: Block, b2: Block): Boolean {
        if (b1.rx >= b2.lx && b2.rx >= b1.lx && b1.ty >= b2.by && b2.ty >= b1.by) {
            for (x in b1.lx..b1.rx) {
                for (y in b1.by..b1.ty) {
                    if (b1.isOccupied(x, y) && b2.isOccupied(x, y)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun puzzleOne(input: List<String>): Any {
        val jets = input[0]
        val blocks = mutableListOf<Block>()

        fun printTower(b: Block) {
            val maxY = max(blocks.maxOfOrNull { it.ty } ?: 0, b.ty)
            for (y in maxY downTo -1) {
                for (x in -1..7) {
                    when {
                        y == -1 && (x == -1 || x == 7) -> print('+')
                        y == -1 -> print('-')
                        x == -1 || x == 7 -> print('|')
                        b.isOccupied(x, y) -> print('@')
                        blocks.any { it.isOccupied(x, y) } -> print('#')
                        else -> print('.')
                    }
                }
                println()
            }
            println()
        }

        fun spawn(): Block {
            val rock = blocks.size % rocks.size
            val y = (blocks.maxOfOrNull { it.ty } ?: -1) + 4
            return Block(rock, 2, y)
        }

        fun Block.collides() = collidesWithWall() || blocks.any { isColliding(this, it) }

        fun Block.moveSides(c: Char) {
            when (c) {
                '>' -> {
                    lx += 1
                    if (collides()) lx -= 1
                }

                '<' -> {
                    lx -= 1
                    if (collides()) lx += 1
                }
            }
        }

        fun Block.attemptFall(): Boolean {
            by -= 1
            return if (collides()) {
                by += 1
                false
            } else {
                true
            }
        }

        var jet = 0
        fun blow(b: Block) {
            b.moveSides(jets[jet])
            jet = (jet + 1) % jets.length
        }

        repeat(2022) {
            val b = spawn()
//            printTower(b)
            blow(b)
//            printTower(b)
            while (b.attemptFall()) {
//                printTower(b)
                blow(b)
//                printTower(b)
            }
            printTower(b)
            blocks.add(b)
        }

        return blocks.maxOf { it.ty } + 1
    }

    fun puzzleTwo(input: List<String>): Any {
        val jets = input[0]
        val blocks = mutableListOf<Block>()

        fun spawn(): Block {
            val rock = blocks.size % rocks.size
            val y = (blocks.maxOfOrNull { it.ty } ?: -1) + 4
            return Block(rock, 2, y)
        }

        fun Block.collides() = collidesWithWall() || blocks.any { isColliding(this, it) }

        fun Block.moveSides(c: Char) {
            when (c) {
                '>' -> {
                    lx += 1
                    if (collides()) lx -= 1
                }

                '<' -> {
                    lx -= 1
                    if (collides()) lx += 1
                }
            }
        }

        fun Block.attemptFall(): Boolean {
            by -= 1
            return if (collides()) {
                by += 1
                false
            } else {
                true
            }
        }

        var jet = 0
        fun blow(b: Block) {
            b.moveSides(jets[jet])
            jet = (jet + 1) % jets.length
        }

        data class Position(val jet: Int, val rock: Int)

        val positions = mutableMapOf<Position, Int>()
        val positionNumbers = mutableMapOf<Position, Int>()

        fun checkForCycle(size: Int): Boolean {
            return (1..size).all {
                blocks[blocks.size - it].lx == blocks[blocks.size - it - size].lx
            }
        }

        fun searchForCycle(): Int {
            while (blocks.size < 1_000_000) {
                val b = spawn()
                val position = Position(jet, b.rock)
                positions.merge(position, 1, Int::plus)
                if (positions[position] == 3) {
                    val size = blocks.size - positionNumbers[position]!!
                    if (checkForCycle(size)) {
                        println("Found cycle at position: ${blocks.size} of size $size")
                        return size
                    }
                } else {
                    positionNumbers[position] = blocks.size
                }

                blow(b)
                while (b.attemptFall()) {
                    blow(b)
                }
                blocks.add(b)
            }
            throw RuntimeException("Cycle not found")
        }

        val cycleSize = searchForCycle()
        val headSize = blocks.size - cycleSize * 2
        val headHeight = blocks[blocks.size - 1 - cycleSize * 2].ty + 1
        val cycleHeight = blocks[blocks.size - 1].ty - blocks[blocks.size - 1 - cycleSize].ty

        val totalBlocks = 1_000_000_000_000L

        val cyclesCount = (totalBlocks - headSize) / cycleSize
        val remainBlocks = (totalBlocks - headSize) % cycleSize

        val remainBlocksHeight =
            blocks[blocks.size - 1 - cycleSize + remainBlocks.toInt()].ty - blocks[blocks.size - 1 - cycleSize].ty

        println("Cycle size: $cycleSize Head size: $headSize")
        println("Cycle height: $cycleHeight Head height: $headHeight")
        println("Cycles count: $cyclesCount Remain blocks: $remainBlocks Remain blocks height: $remainBlocksHeight")
        return headHeight + cyclesCount * cycleHeight + remainBlocksHeight
    }


    println(puzzleOne(input))
    println(puzzleTwo(input))
}