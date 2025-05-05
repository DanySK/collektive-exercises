package collektive.exercises

import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.alchemist.model.positions.Euclidean2DPosition
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.stdlib.spreading.distanceTo
import kotlin.math.round

fun Aggregate<Int>.metricDistance(simulatedDevice: CollektiveDevice<Euclidean2DPosition>): Field<Int, Double> {
    val localPosition = simulatedDevice.environment.getPosition(simulatedDevice.node)
    return neighboring(localPosition).mapValues {
        localPosition.distanceTo(it)
    }
}

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

fun Aggregate<Int>.entrypoint(simulatedDevice: CollektiveDevice<Euclidean2DPosition>): Any? {

    // Gradient
//    val programOutput = myDistanceTo(simulatedDevice.localId == 0, metricDistance(simulatedDevice))

    // Channel
//    val programOutput = channelAroundObstacles(
//        isObstacle = simulatedDevice.isObstacle(),
//        source = localId == 19,
//        destination = localId == 270,
//        width = 0.5,
//        metric = metricDistance(simulatedDevice),
//    ).toNiceLookingDouble()

//    val programOutput =
//        if (simulatedDevice.isObstacle()) 0.0 else myDistanceTo(localId == 19, metricDistance(simulatedDevice))

//    // Distance to destination
//    val programOutput =
//        if (simulatedDevice.isObstacle()) 0.0 else myDistanceTo(localId == 270, metricDistance(simulatedDevice))

//    // Sum of the distances
//    val programOutput = if (simulatedDevice.isObstacle()) {
//        0.0
//    } else {
//        listOf(19, 270).sumOf {
//            alignedOn(it) { myDistanceTo(localId == it, metricDistance(simulatedDevice)) }
//        }
//    }

//    // Broadcast
//    val programOutput = broadcast(localId == 19, localId).toDouble()

//    // Minimum distance broadcast
//    val programOutput = distance(localId == 19, localId == 270, metricDistance(simulatedDevice))

    // Distance from library
//    val programOutput = distanceFromLibrary(19, 270, metricDistance(simulatedDevice))

    // Channel from library
    val programOutput =
        channelFromLibrary(simulatedDevice.isObstacle(), 19, 270, 0.5, metricDistance(simulatedDevice))
            .toNiceLookingDouble()
    return round(programOutput * 1000) / 1000.0
}
