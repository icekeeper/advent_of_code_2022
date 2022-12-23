import java.io.File
import java.util.StringTokenizer

fun main() {
    val input = File("input/day22.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private enum class TileType {
    OPEN, SOLID, EMPTY
}

private data class BoardPosition(val row: Int, val col: Int)
private enum class BoardDirection {
    RIGHT, DOWN, LEFT, UP;

    fun rotateRight() = BoardDirection.values()[(this.ordinal + 1) % 4]
    fun rotateLeft() = BoardDirection.values()[(this.ordinal + 3) % 4]
}

private class BoardMap(val tiles: Array<Array<TileType>>) {

    val visited = mutableMapOf<BoardPosition, BoardDirection>()

    fun step(from: BoardPosition, d: BoardDirection): BoardPosition? {
        var (row, col) = from
        visited[from] = d
        fun next() = when (d) {
            BoardDirection.UP -> row = (row - 1 + tiles.size) % tiles.size
            BoardDirection.DOWN -> row = (row + 1) % tiles.size
            BoardDirection.LEFT -> col = (col - 1 + tiles[0].size) % tiles[0].size
            BoardDirection.RIGHT -> col = (col + 1) % tiles[0].size
        }
        do {
            next()
        } while (tiles[row][col] == TileType.EMPTY)

        return if (tiles[row][col] == TileType.OPEN) BoardPosition(row, col) else null
    }

    fun cubeStep(from: BoardPosition, d: BoardDirection): Pair<BoardPosition, BoardDirection>? {
        val side = 50
        var (row, col) = from
        var dir = d
        visited[from] = d
        when (d) {
            BoardDirection.UP -> row -= 1
            BoardDirection.DOWN -> row += 1
            BoardDirection.LEFT -> col -= 1
            BoardDirection.RIGHT -> col += 1
        }

        //example rules
//        when {
//            row == -1 -> {
//                println("Condition 1 row: $row col: $col")
//                dir = BoardDirection.DOWN
//                row = sideLength
//                col = sideLength * 3 - 1 - col
//            }
//
//            col == sideLength * 2 - 1 && row < sideLength && dir == BoardDirection.LEFT -> {
//                println("Condition 2 row: $row col: $col")
//                dir = BoardDirection.DOWN
//                col = sideLength + row
//                row = sideLength
//            }
//
//            col == sideLength * 3 && row < sideLength -> {
//                println("Condition 3 row: $row col: $col")
//                dir = BoardDirection.LEFT
//                row = sideLength * 3 - 1 - row
//                col = sideLength * 4 - 1
//            }
//
//            row == sideLength - 1 && col < sideLength -> {
//                println("Condition 4 row: $row col: $col")
//                dir = BoardDirection.DOWN
//                row = 0
//                col = sideLength * 3 - 1 - col
//            }
//
//            row == sideLength - 1 && col < sideLength * 2 -> {
//                println("Condition 5 row: $row col: $col")
//                dir = BoardDirection.RIGHT
//                row = col - sideLength
//                col = sideLength * 2
//            }
//
//            col == -1 -> {
//                println("Condition 6 row: $row col: $col")
//                dir = BoardDirection.UP
//                col = sideLength * 4 - 1 - (row - sideLength)
//                row = sideLength * 3 - 1
//            }
//
//            col == sideLength * 3 && row < sideLength * 2 && dir == BoardDirection.RIGHT -> {
//                println("Condition 7 row: $row col: $col")
//                dir = BoardDirection.DOWN
//                col = sideLength * 4 - 1 - (row - sideLength)
//                row = sideLength * 2
//            }
//
//            row == sideLength * 2 && col < sideLength -> {
//                println("Condition 8 row: $row col: $col")
//                dir = BoardDirection.UP
//                row = sideLength * 3 - 1
//                col = sideLength * 3 - 1 - (col)
//            }
//
//            row == sideLength * 2 && col < sideLength * 2 && dir == BoardDirection.DOWN -> {
//                println("Condition 9 row: $row col: $col")
//                dir = BoardDirection.RIGHT
//                row = sideLength * 3 - 1 - (col - sideLength)
//                col = sideLength * 2
//            }
//
//            row == sideLength * 2 - 1 && col >= sideLength * 3 -> {
//                println("Condition 10 row: $row col: $col")
//                dir = BoardDirection.LEFT
//                row = sideLength * 2 - 1 - (col - sideLength * 3)
//                col = sideLength * 3 - 1
//            }
//
//            col == sideLength * 2 - 1 && row >= sideLength * 2 -> {
//                println("Condition 11 row: $row col: $col")
//                dir = BoardDirection.UP
//                col = sideLength * 2 - 1 - (row - sideLength * 2)
//                row = sideLength * 2 - 1
//            }
//
//            col == sideLength * 4 -> {
//                println("Condition 12 row: $row col: $col")
//                dir = BoardDirection.LEFT
//                row = sideLength - 1 - (row - sideLength * 2)
//                col = sideLength * 3 - 1
//            }
//
//            row == sideLength * 3 && col < sideLength * 3 -> {
//                println("Condition 13 row: $row col: $col")
//                dir = BoardDirection.UP
//                row = sideLength * 2 - 1
//                col = sideLength - 1 - (col - sideLength * 2)
//            }
//
//            row == sideLength * 3 && col >= sideLength * 3 -> {
//                println("Condition 14 row: $row col: $col")
//                dir = BoardDirection.RIGHT
//                row = sideLength * 2 - 1 - (col - sideLength * 3)
//                col = 0
//            }
//

// Actual map has different layout, so I have to rewrite all the rules.
//                    *1* *2*
//                    3** **4
//                    *** *5*
//
//                    ***
//                    6*5
//                    ***
//
//                *6* ***
//                3** **4
//                *** *7*
//
//                ***
//                1*7
//                *2*

        //actual rules
        when {
            //1
            row == -1 && col < side * 2 -> {
                println("Condition 1-1 row: $row col: $col")
                dir = BoardDirection.RIGHT
                row = side * 3 + (col - side)
                col = 0
            }

            col == -1 && row >= side * 3 -> {
                println("Condition 1-2 row: $row col: $col")
                dir = BoardDirection.DOWN
                col = side + (row - side * 3)
                row = 0
            }

            //2
            row == -1 && col >= side * 2 -> {
                println("Condition 2-1 row: $row col: $col")
                dir = BoardDirection.UP
                col -= side * 2
                row = side * 4 - 1
            }

            row == side * 4 -> {
                println("Condition 2-2 row: $row col: $col")
                dir = BoardDirection.DOWN
                col += side * 2
                row = 0
            }

            //3
            col == side - 1 && row < side -> {
                println("Condition 3-1 row: $row col: $col")
                dir = BoardDirection.RIGHT
                row = side * 3 - 1 - row
                col = 0
            }

            col == -1 && row < side * 3 -> {
                println("Condition 3-2 row: $row col: $col")
                dir = BoardDirection.RIGHT
                row = side * 3 - 1 - row
                col = side
            }

            //4
            col == side * 3 -> {
                println("Condition 4-1 row: $row col: $col")
                dir = BoardDirection.LEFT
                row = side * 3 - 1 - row
                col = side * 2 - 1
            }

            col == side * 2 && row >= side * 2 -> {
                println("Condition 4-2 row: $row col: $col")
                dir = BoardDirection.LEFT
                row = side * 3 - 1 - row
                col = side * 3 - 1
            }

            //5
            row == side && col >= side * 2 && dir == BoardDirection.DOWN -> {
                println("Condition 5-1 row: $row col: $col")
                dir = BoardDirection.LEFT
                row = side + (col - side * 2)
                col = side * 2 - 1
            }

            col == side * 2 && row >= side -> {
                println("Condition 5-2 row: $row col: $col")
                dir = BoardDirection.UP
                col = side * 2 + (row - side)
                row = side - 1
            }

            //6
            col == side - 1 && row < side * 2 && dir == BoardDirection.LEFT -> {
                println("Condition 6-1 row: $row col: $col")
                dir = BoardDirection.DOWN
                col = row - side
                row = side * 2
            }

            row == side * 2 - 1 && col < side -> {
                println("Condition 6-2 row: $row col: $col")
                dir = BoardDirection.RIGHT
                row = side + col
                col = side
            }

            //7
            row == side * 3 && col >= side && dir == BoardDirection.DOWN -> {
                println("Condition 7-1 row: $row col: $col")
                dir = BoardDirection.LEFT
                row = side * 3 + (col - side)
                col = side - 1
            }

            col == side && row >= side * 3 -> {
                println("Condition 7-2 row: $row col: $col")
                dir = BoardDirection.UP
                col = side + (row - side * 3)
                row = side * 3 - 1
            }
        }

        return if (tiles[row][col] == TileType.OPEN) Pair(BoardPosition(row, col), dir) else null
    }

    fun print() {
        tiles.forEachIndexed { i, row ->
            row.forEachIndexed { j, tile ->
                val p = BoardPosition(i, j)
                when {
                    visited.containsKey(p) -> when (visited[p]) {
                        BoardDirection.RIGHT -> print('>')
                        BoardDirection.LEFT -> print('<')
                        BoardDirection.DOWN -> print('v')
                        BoardDirection.UP -> print('^')
                        null -> {}
                    }

                    tile == TileType.EMPTY -> print(' ')
                    tile == TileType.OPEN -> print('.')
                    tile == TileType.SOLID -> print('#')
                }
            }
            println()
        }
    }
}

private fun puzzleOne(input: List<String>): Any {
    val field = input.dropLast(2)
    val rowSize = field.maxOf { it.length }
    val map = BoardMap(Array(field.size) { row ->
        Array(rowSize) { col ->
            if (col !in field[row].indices) TileType.EMPTY
            else when (field[row][col]) {
                '.' -> TileType.OPEN
                '#' -> TileType.SOLID
                else -> TileType.EMPTY
            }
        }
    })

    var position = BoardPosition(0, map.tiles[0].indexOfFirst { it == TileType.OPEN })
    var direction = BoardDirection.RIGHT

    val instructions = input.last()

    val tokenizer = StringTokenizer(instructions, "RL", true)
    while (tokenizer.hasMoreTokens()) {
        when (val instruction = tokenizer.nextToken()) {
            "R" -> direction = direction.rotateRight()
            "L" -> direction = direction.rotateLeft()
            else -> repeat(instruction.toInt()) {
                val nextPosition = map.step(position, direction)
                if (nextPosition != null) position = nextPosition
            }
        }
    }
    map.print()
    return 1000 * (position.row + 1) + 4 * (position.col + 1) + direction.ordinal
}

private fun puzzleTwo(input: List<String>): Any {
    val field = input.dropLast(2)
    val rowSize = field.maxOf { it.length }
    val map = BoardMap(Array(field.size) { row ->
        Array(rowSize) { col ->
            if (col !in field[row].indices) TileType.EMPTY
            else when (field[row][col]) {
                '.' -> TileType.OPEN
                '#' -> TileType.SOLID
                else -> TileType.EMPTY
            }
        }
    })

    var position = BoardPosition(0, map.tiles[0].indexOfFirst { it == TileType.OPEN })
    var direction = BoardDirection.RIGHT

    val instructions = input.last()

    val tokenizer = StringTokenizer(instructions, "RL", true)
    while (tokenizer.hasMoreTokens()) {
        when (val instruction = tokenizer.nextToken()) {
            "R" -> direction = direction.rotateRight()
            "L" -> direction = direction.rotateLeft()
            else -> repeat(instruction.toInt()) {
                val pair = map.cubeStep(position, direction)
                if (pair != null) {
                    position = pair.first
                    direction = pair.second
                }
            }
        }
    }
    map.print()
    return 1000 * (position.row + 1) + 4 * (position.col + 1) + direction.ordinal
}