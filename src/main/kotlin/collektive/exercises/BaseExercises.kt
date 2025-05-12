package collektive.exercises

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate

fun Aggregate<Int>.getLocalId(): Int = localId

/**
 * Evolves a local value over time by incrementing it at each computational round.
 * Starts from 0 and increases by 1 at each execution, maintaining the value across rounds.
 */
fun Aggregate<Int>.incrementValue(): Int = 0

/**
 * Retrieves the highest localId among the current node's neighbors.
 *
 * This function scans the neighborhood for the maximum `localId` value (excluding the current node),
 * returning 0 if the node has no neighbors.
 */
fun Aggregate<Int>.maxValueFromNeighbours(): Int = 0

/**
 * Find the node with the smallest ID in the network, without using the standard library functions.
 *
 * Remove the node with the smallest ID from the network. What happens? Try to predict it.
 */
fun Aggregate<Int>.searchSourceNS(): Boolean = false

/**
 * Find the node with the smallest ID in the network, using the standard library functions.
 */
fun Aggregate<Int>.searchSource(): Boolean = false

/**
 * Compute the *distances* between any node and the *source* using *Hop-Distance* algorithm.
 */
fun Aggregate<Int>.distanceToSource(): Int = 0

/**
 * Compute the *distances* between any node and the *source* using *Bellman-Ford* algorithm and [metric] passed.
 */
fun Aggregate<Int>.distanceToSource(metric: Field<Int, Double>): Double = 0.0

/**
 * Estimates the **diameter of the network** (i.e., the maximum hop-distance between any two devices).
 *
 * The result is computed using hop count (not metric distance) and broadcast to all nodes.
 */
fun Aggregate<Int>.networkDiameter(): Double = 0.0