import java.io.File
import java.util.regex.Pattern
import kotlin.math.max

fun main() {
    val input = File("input/day19.txt").readLines()
    println(puzzleOne(input))
    println(puzzleTwo(input))
}

private data class Inventory(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geodes: Int = 0,
    val oreRobots: Int = 1,
    val clayRobots: Int = 0,
    val obsidianRobots: Int = 0,
    val geodeRobots: Int = 0
) {
    fun canBuildOreRobot(b: Blueprint) = b.oreRobotOreCost <= ore
    fun canBuildClayRobot(b: Blueprint) = b.clayRobotOreCost <= ore
    fun canBuildObsidianRobot(b: Blueprint) = b.obsidianRobotOreCost <= ore && b.obsidianRobotClayCost <= clay
    fun canBuildGeodeRobot(b: Blueprint) = b.geodeRobotOreCost <= ore && b.geodeRobotObsidianCost <= obsidian

    fun buildOreRobot(b: Blueprint) = copy(
        ore = ore - b.oreRobotOreCost,
        oreRobots = oreRobots + 1
    )

    fun buildClayRobot(b: Blueprint) = copy(
        ore = ore - b.clayRobotOreCost,
        clayRobots = clayRobots + 1
    )

    fun buildObsidianRobot(b: Blueprint) = copy(
        ore = ore - b.obsidianRobotOreCost,
        clay = clay - b.obsidianRobotClayCost,
        obsidianRobots = obsidianRobots + 1
    )

    fun buildGeodeRobot(b: Blueprint) = copy(
        ore = ore - b.geodeRobotOreCost,
        obsidian = obsidian - b.geodeRobotObsidianCost,
        geodeRobots = geodeRobots + 1
    )

    fun mine() = copy(
        ore = ore + oreRobots,
        clay = clay + clayRobots,
        obsidian = obsidian + obsidianRobots,
        geodes = geodes + geodeRobots
    )
}

private data class Blueprint(
    val number: Int,
    val oreRobotOreCost: Int,
    val clayRobotOreCost: Int,
    val obsidianRobotOreCost: Int,
    val obsidianRobotClayCost: Int,
    val geodeRobotOreCost: Int,
    val geodeRobotObsidianCost: Int
)

private fun maxGeodes(b: Blueprint, inv: Inventory = Inventory(), m: Int, currentMax: Int = 0): Int {
    if (m == 0) return inv.geodes

    val geodeLimit = inv.geodes + (inv.geodeRobots * 2 + (m - 1)) * m / 2
    if (geodeLimit < currentMax) {
        return currentMax
    }

    var max = currentMax
    val nextInv = inv.mine()
    if (inv.canBuildGeodeRobot(b)) {
        max = max(max, maxGeodes(b, nextInv.buildGeodeRobot(b), m - 1, max))
    }
    if (inv.canBuildObsidianRobot(b) && b.geodeRobotObsidianCost > inv.obsidianRobots) {
        max = max(max, maxGeodes(b, nextInv.buildObsidianRobot(b), m - 1, max))
    }
    if (inv.canBuildClayRobot(b) && b.obsidianRobotClayCost > inv.clayRobots) {
        max = max(max, maxGeodes(b, nextInv.buildClayRobot(b), m - 1, max))
    }
    if (inv.canBuildOreRobot(b) &&
        (b.geodeRobotOreCost > inv.oreRobots ||
                b.obsidianRobotOreCost > inv.oreRobots ||
                b.clayRobotOreCost > inv.oreRobots ||
                b.oreRobotOreCost > inv.oreRobots)
    ) {
        max = max(max, maxGeodes(b, nextInv.buildOreRobot(b), m - 1, max))
    }

    max = max(max, maxGeodes(b, nextInv, m - 1, max))
    return max
}

val blueprintPattern: Pattern = Pattern.compile(
    """Blueprint (\d+): Each ore robot costs (\d+) ore\. Each clay robot costs (\d+) ore\. Each obsidian robot costs (\d+) ore and (\d+) clay\. Each geode robot costs (\d+) ore and (\d+) obsidian\."""
)

private fun parseBlueprint(s: String): Blueprint {
    val matcher = blueprintPattern.matcher(s)
    matcher.matches()
    return Blueprint(
        number = matcher.group(1).toInt(),
        oreRobotOreCost = matcher.group(2).toInt(),
        clayRobotOreCost = matcher.group(3).toInt(),
        obsidianRobotOreCost = matcher.group(4).toInt(),
        obsidianRobotClayCost = matcher.group(5).toInt(),
        geodeRobotOreCost = matcher.group(6).toInt(),
        geodeRobotObsidianCost = matcher.group(7).toInt()
    )
}

private fun puzzleOne(input: List<String>): Any {
    val blueprints = input.map(::parseBlueprint)

    return blueprints.sumOf {
        val maxGeodes = maxGeodes(it, m = 24)
        println("Blueprint: ${it.number} max geodes: $maxGeodes")
        it.number * maxGeodes
    }
}

private fun puzzleTwo(input: List<String>): Any {
    val blueprints = input.map(::parseBlueprint)

    return blueprints.take(3).fold(1) { acc, b ->
        val maxGeodes = maxGeodes(b, m = 32)
        println("Blueprint: ${b.number} max geodes: $maxGeodes")
        acc * maxGeodes
    }
}