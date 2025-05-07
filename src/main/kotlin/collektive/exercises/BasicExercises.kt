package collektive.exercises

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.collektive.alchemist.device.sensors.EnvironmentVariables
import it.unibo.collektive.stdlib.spreading.hopDistanceTo
import it.unibo.collektive.stdlib.spreading.gradientCast
import it.unibo.collektive.stdlib.spreading.bellmanFordGradientCast
import it.unibo.collektive.stdlib.fields.minValue
import it.unibo.collektive.stdlib.fields.maxValue
import it.unibo.collektive.aggregate.api.share

/**
 * Select a node identified as [source], chosen by finding the node with [minimum uid] 
 * in the network.
*/
fun Aggregate<Int>.searchSource(environment: EnvironmentVariables): Boolean = share(localId){ field ->
        field.minValue(localId)
    }.let { minValue ->
        val isSource = localId == minValue
        environment["isSource"] = isSource
        isSource
    }

/**
 * Compute the [distances] between any node and the [source].
*/
fun Aggregate<Int>.distanceToSource(environment: EnvironmentVariables): Int = hopDistanceTo(searchSource(environment))

/**
 * Compute the [distances] between any node and the [source] using [Bellman-Ford] algorithm.
*/
fun Aggregate<Int>.distanceToSource(environment: EnvironmentVariables, distanceSensor: CollektiveDevice<*>): Int = bellmanFordGradientCast(
        source = searchSource(environment),
        local = 0,
        accumulateData = { _, _, dist ->
            dist + 1
        },
        metric = with(distanceSensor) { distances() }
    )

/**
 * Calculate in the [source] an estimate of the true [diameter] of the network (the maximum distance of a device in the network).
 * Broadcast the [diameter] to every node in the network.
*/ 
fun Aggregate<Int>.networkDiameter(environment: EnvironmentVariables, distanceSensor: CollektiveDevice<*>): Int {
    val isFurthest = isMaxValue(distanceToSource(environment))
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
fun Aggregate<Int>.isMaxValue(localValue: Int): Boolean = share(localValue){ field ->
        field.maxValue(localValue)
    }.let { value ->
        localValue == value
    }