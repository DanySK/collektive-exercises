package collektive.exercises

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.stdlib.fields.maxValue
import it.unibo.collektive.stdlib.fields.minValue
import it.unibo.collektive.stdlib.spreading.*
import kotlin.random.Random

fun Aggregate<Int>.getLocalId(): Int = localId

/**
 * Evolves a local value over time by incrementing it at each computational round.
 * Starts from 0 and increases by 1 at each execution, maintaining the value across rounds.
 */
fun Aggregate<Int>.incrementValue(): Int = evolve(0) { value -> value + 1 }

/**
 * Retrieves the highest localId among the current node's neighbors.
 *
 * This function scans the neighborhood for the maximum `localId` value (excluding the current node),
 * returning 0 if the node has no neighbors.
 */
fun Aggregate<Int>.maxValueFromNeighbours(): Int = neighboring(localId).maxValue(0)

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
 * Compute the *distances* between any node and the *source* using *Hop-Distance* algorithm.
*/
fun Aggregate<Int>.distanceToSource(): Int = hopDistanceTo(searchSource())

/**
 * Compute the *distances* between any node and the *source* using *Bellman-Ford* algorithm and [metric] passed.
*/
fun Aggregate<Int>.distanceToSource(metric: Field<Int, Double>): Double = distanceTo(searchSource(), metric = metric)

/**
 * Calculate in the *source* an estimate of the true **diameter of the network** (the maximum distance of a device in the network).
 * Broadcast the *diameter* to every node in the network.
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
