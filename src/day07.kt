fun main() {
    val input = java.io.File("input/day07.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

sealed interface Node {
    val name: String
    val size: Long
}

data class Folder(
    override val name: String,
    val parent: Folder?,
    val children: MutableList<Node> = mutableListOf()
) : Node {
    override val size: Long by lazy {
        children.sumOf { it.size }
    }

    fun recursiveChildren(): Sequence<Folder> = sequence {
        yield(this@Folder)
        children.filterIsInstance<Folder>().forEach {
            yieldAll(it.recursiveChildren())
        }
    }
}

data class File(override val name: String, override val size: Long) : Node

private fun parseCommands(input: List<String>): Folder {
    val commands = mutableListOf<MutableList<String>>()
    input.forEach {
        when {
            it.startsWith('$') -> commands.add(mutableListOf(it))
            else -> commands.last().add(it)
        }
    }

    val root = Folder("/", null)
    var pointer = root

    commands.forEach { cmd ->
        when {
            cmd.first().startsWith("$ cd") -> {
                val name = cmd.first().substring(5)
                pointer = when (name) {
                    "/" -> root
                    ".." -> pointer.parent!!
                    else -> pointer.children.filterIsInstance<Folder>().find { it.name == name }!!
                }
            }

            cmd.first().startsWith("$ ls") -> {
                cmd.asSequence().drop(1).forEach {
                    val parts = it.split(" ")
                    if (parts[0] == "dir") {
                        pointer.children.add(Folder(parts[1], pointer))
                    } else {
                        pointer.children.add(File(parts[1], parts[0].toLong()))
                    }
                }
            }
        }
    }
    return root
}

private fun puzzleOne(input: List<String>): Any {
    val root = parseCommands(input)
    return root.recursiveChildren().filter { it.size <= 100000 }.sumOf { it.size }
}

private fun puzzleTwo(input: List<String>): Any {
    val root = parseCommands(input)
    val toFree = root.size - 40_000_000
    return root.recursiveChildren().filter { it.size >= toFree }.minBy { it.size }.size
}

private fun print(n: Node, ident: Int = 0) {
    repeat(ident) { print(" ") }
    when (n) {
        is File -> println("- ${n.name} (file, size=${n.size})")
        is Folder -> {
            println("- ${n.name} (dir)")
            n.children.forEach { print(it, ident + 2) }
        }
    }
}
