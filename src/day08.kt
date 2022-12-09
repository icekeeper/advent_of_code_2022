import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min

fun main() {
    val input = File("input/day08.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private fun puzzleOne(input: List<String>): Any {
    val trees = input.map { it.map { c -> c.digitToInt() } }
    val n = input.size
    val m = input.first().length

    val visible = Array(n) { Array(m) { false } }

    for (i in 0 until n) {
        var max = -1
        for (j in 0 until m - 1) {
            visible[i][j] = visible[i][j] || (max < trees[i][j])
            max = max(max, trees[i][j])
        }
        max = -1
        for (j in m - 1 downTo 1) {
            visible[i][j] = visible[i][j] || (max < trees[i][j])
            max = max(max, trees[i][j])
        }
    }

    for (j in 0 until m) {
        var max = -1
        for (i in 0 until n - 1) {
            visible[i][j] = visible[i][j] || (max < trees[i][j])
            max = max(max, trees[i][j])
        }
        max = -1
        for (i in n - 1 downTo 1) {
            visible[i][j] = visible[i][j] || (max < trees[i][j])
            max = max(max, trees[i][j])
        }
    }



    return visible.sumOf { line -> line.count { it } }
}

private fun puzzleTwo(input: List<String>): Any {
    val trees = input.map { it.map { c -> c.digitToInt() } }
    val n = input.size
    val m = input.first().length

    fun score(i: Int, j: Int): Long {
        var l = max(j - 1, 0)
        while (l > 0 && trees[i][l] < trees[i][j]) l--

        var r = min(j + 1, m - 1)
        while (r < m - 1 && trees[i][r] < trees[i][j]) r++

        var u = max(i - 1, 0)
        while (u > 0 && trees[u][j] < trees[i][j]) u--

        var d = min(i + 1, n - 1)
        while (d < n - 1 && trees[d][j] < trees[i][j]) d++

        return 1L * (j - l) * (r - j) * (i - u) * (d - i)
    }

//    for(i in trees.indices) {
//        for(j in trees[i].indices) {
//            print("${score(i, j)} ")
//        }
//        println()
//    }

    return trees.indices.flatMap { i -> trees[i].indices.map { j -> score(i, j) } }.max()
}