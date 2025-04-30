package collektive.exercises

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.collektive.stdlib.spreading.gossipMin
import it.unibo.collektive.stdlib.spreading.gossipMax
import it.unibo.collektive.stdlib.spreading.hopDistanceTo
import it.unibo.collektive.stdlib.spreading.gradientCast

/**
 * Select a node identified as [source], chosen by finding the node with [minimum uid] 
 * in the network, assuming that the diameter of the network is no more than 10 hops.
*/
fun Aggregate<Int>.searchSource(): Boolean =
    gossipMin(localId).let { minValue ->
        localId == minValue
    }

/**
 * Compute the [distances] between any node and the [source] using the adaptive bellman-ford algorithm.
*/
fun Aggregate<Int>.distanceToSource() = hopDistanceTo(searchSource())

/**
 * Calculate in the [source] an estimate of the true [diameter] of the network (the maximum distance of a device in the network).
 * 
 * Broadcast the [diameter] to every node in the network.
*/ 
fun Aggregate<Int>.networkDiameter(distanceSensor: CollektiveDevice<*>): Int {
    val isFurthest = isMaxValue(distanceToSource())
    val distanceToFurthest = hopDistanceTo(isFurthest)
    val flagNodeWithMaxHopToFurthest = isMaxValue(distanceToFurthest)
    val broadcastMessage = broadcast(
        distanceSensor, 
        from = flagNodeWithMaxHopToFurthest, 
        payload = distanceToFurthest.toDouble()
    ).toInt()
    return when {
        distanceToFurthest <= broadcastMessage -> broadcastMessage
        else -> distanceToFurthest
    }
}

/**
 * Computes the [gradientCast] from the [source] with the [value] that is the distance from the [source] to the target.
 */
fun Aggregate<Int>.broadcast(distanceSensor: CollektiveDevice<*>, from: Boolean, payload: Double): Double =
    gradientCast(
        source = from,
        local = payload,
        metric = with(distanceSensor) { distances() },
    )
    
/**
 * Function that identifies the [maximum value] and returns true if the passed value is the maximum.
 */
fun Aggregate<Int>.isMaxValue(localValue: Int): Boolean = 
    gossipMax(localValue).let { maxValue ->
        localValue == maxValue
    }