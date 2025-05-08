package collektive.exercises

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.alchemist.device.sensors.EnvironmentVariables
import it.unibo.collektive.stdlib.spreading.hopDistanceTo
import it.unibo.collektive.stdlib.spreading.gradientCast
import it.unibo.collektive.stdlib.spreading.bellmanFordGradientCast
import it.unibo.collektive.stdlib.fields.minValue
import it.unibo.collektive.stdlib.fields.maxValue
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.stdlib.spreading.distanceTo
import it.unibo.collektive.stdlib.spreading.gossipMax
import it.unibo.collektive.stdlib.spreading.gossipMin
import kotlin.random.Random

/**
 * Find the node with the smallest ID in the network, without using the standard library functions.
 *
 * Remove the node with the smallest ID from the network. What happens? Try to predict it.
 */
fun Aggregate<Int>.searchSourceNS(): Boolean = share(localId) { ids -> ids.minValue(localId) } == localId
//inline fun <reified ID: Comparable<ID>> Aggregate<ID>.searchSource(): Boolean =
//    share(localId) { ids -> ids.minValue(localId) } == localId

/**
 * Find the node with the smallest ID in the network, using the standard library functions.
 */
fun Aggregate<Int>.searchSource() = gossipMin(localId) == localId

/**
 * Compute the *distances* between any node and the [source].
*/
fun Aggregate<Int>.distanceToSource(): Int = hopDistanceTo(searchSource())

/**
 * Compute the [distances] between any node and the [source] using [Bellman-Ford] algorithm.
*/
fun Aggregate<Int>.distanceToSource(metric: Field<Int, Double>): Double = distanceTo(searchSource(), metric = metric)

/**
 * Calculate in the [source] an estimate of the true [diameter] of the network (the maximum distance of a device in the network).
 * Broadcast the [diameter] to every node in the network.
*/ 
fun Aggregate<Int>.networkDiameter(metric: Field<Int, Double>): Double {
    val randomId = evolve(Random.Default.nextInt()) { it }
    val distanceFromRandomPoint = distanceTo(
        gossipMin(randomId) == randomId,
        metric = metric
    )
    val isFurthest = gossipMax(distanceFromRandomPoint) == distanceFromRandomPoint
    val distanceToFurthest = distanceTo(isFurthest, metric = metric)
    return gossipMax(distanceToFurthest)
}

/**
 * Computes the [gradientCast] from the [source] with the [value] that is the distance from the [source] to the target.
 */
fun Aggregate<Int>.broadcast(from: Boolean, payload: Double, metric: Field<Int, Double>): Double =
    gradientCast(
        source = from,
        local = payload,
        metric = metric,
    )
