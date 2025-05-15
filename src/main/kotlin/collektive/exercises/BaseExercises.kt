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
 * Estimates the **diameter of the network** (i.e., the maximum hop-distance between any two devices).
 *
 * The result is computed using hop count (not metric distance) and broadcast to all nodes.
 */
fun Aggregate<Int>.networkDiameter(): Int {
    val randomId = evolve(Random.Default.nextInt()) { it }
    val distanceFromRandomPoint = hopDistanceTo(
        gossipMin(randomId) == randomId
    )
    val isFurthest = gossipMax(distanceFromRandomPoint) == distanceFromRandomPoint
    val distanceToFurthest = hopDistanceTo(isFurthest)
    return gossipMax(distanceToFurthest)
}
