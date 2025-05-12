package collektive.exercises

import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.alchemist.model.positions.Euclidean2DPosition
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.stdlib.spreading.distanceTo
import kotlin.math.round

/**
 * Computes a distance metric field between the current node and its neighbors.
 */
fun Aggregate<Int>.metricDistance(simulatedDevice: CollektiveDevice<Euclidean2DPosition>): Field<Int, Double> {
    val localPosition = simulatedDevice.environment.getPosition(simulatedDevice.node)
    return neighboring(localPosition).mapValues {
        localPosition.distanceTo(it)
    }
}

/**
 * Determines whether the current device is positioned within a static obstacle region.
 *
 * The environment is partitioned into rectangular blocks that represent obstacles.
 * If the current device lies within one of these blocks, it is marked as an obstacle.
 *
 * @return True if the node is inside an obstacle area, false otherwise.
 */
fun CollektiveDevice<Euclidean2DPosition>.isObstacle(): Boolean {
    val position = environment.getPosition(node)
    // lower block
    val obstacle = position.x > 3 && position.y in 3.0..5.0 ||
        // left block
        position.x in 3.0..5.0 && position.y in 3.0..17.0 ||
        // upper block
        position.x in 3.0..17.0 && position.y in 15.0..17.0 ||
        // right block
        position.x in 15.0..17.0 && position.y in 7.0..17.0 ||
        // lower middle block
        position.x in 7.0..17.0 && position.y in 7.0..9.0
    return obstacle.also { set("obstacle", obstacle) }
}

private fun Boolean.toNiceLookingDouble() = if (this) 50.0 else 0.0

/**
 * Main entrypoint for the aggregate program.
 *
 * Executes the selected behavior (e.g., `multiLeader`) and prepares its result
 * for visualization.
 */
fun Aggregate<Int>.entrypoint(simulatedDevice: CollektiveDevice<Euclidean2DPosition>): Any? {
    val programOutput: Any = getLocalId() // CHANGE HERE, SELECTING YOUR MAIN FUNCTION
    val toDisplay: Double = when(programOutput) {
        is Number -> programOutput.toDouble()
        is Boolean -> programOutput.toNiceLookingDouble()
        else -> error("Unexpected output type: ${programOutput::class}")
    }
    return round(toDisplay * 1000) / 1000.0
}
