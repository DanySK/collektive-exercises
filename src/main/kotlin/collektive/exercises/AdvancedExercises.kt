package collektive.exercises

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.stdlib.consensus.boundedElection
import it.unibo.collektive.stdlib.spreading.distanceTo
import it.unibo.collektive.stdlib.spreading.gossipMax
import it.unibo.collektive.stdlib.spreading.gossipMin
import it.unibo.collektive.stdlib.spreading.hopGradientCast
import kotlin.math.abs
import kotlin.math.hypot

/**
 * Defined a data class to represent the association between a [sourceID] node and its [distance].
 */
data class SourceDistance(val sourceID: Int, val distance: Int)

/**
 * Draws a bullseye pattern based on network distances and node positions.
 *
 * This function identifies two distant nodes (extremes) in the network to define a main axis,
 * then computes an approximate center point (intersection of two diagonals),
 * and finally assigns a value based on the distance from this central node, creating concentric zones.
 *
 * The returned value is intended for visualization (e.g., as a color gradient from 0 to 100),
 * allowing the rendering of a bullseye pattern across the network.
 */
fun Aggregate<Int>.bullsEye(metric: Field<Int, Double>): Int {
    // Creates a gradient from a randomly chosen node (using gossipMin), measuring distances based on the provided metric.
    val distToRandom = distanceTo(gossipMin(localId) == localId, metric = metric)

    // Finds the node that is farthest from the random starting node. This will serve as the first “extreme” of the network.
    val firstExtreme = gossipMax(distToRandom to localId, compareBy { it.first }).second

    // Builds a distance gradient starting from the first extreme node.
    val distanceToExtreme = distanceTo(firstExtreme == localId, metric = metric)

    // Finds the node that is farthest from the first extreme.
    // This defines the other end of the main axis (the second “extreme”).
    val (distanceBetweenExtremes, otherExtreme) =
        gossipMax(distanceToExtreme to localId, compareBy { it.first })

    // Builds a distance gradient from the second extreme.
    val distanceToOtherExtreme = distanceTo(otherExtreme == localId, metric = metric)

    // Approximates the center of the network by computing the intersection of diagonals between the two extremes,
    // and finds the closest node to that point.

    val distanceFromMainDiameter = abs(distanceBetweenExtremes - distanceToExtreme - distanceToOtherExtreme)
    val distanceFromOpposedDiagonal = abs(distanceToExtreme - distanceToOtherExtreme)
    val approximateDistance = hypot(distanceFromOpposedDiagonal, distanceFromMainDiameter)
    val centralNode = gossipMin(approximateDistance to localId, compareBy { it.first }).second

    // Measures how far each node is from the computed center.
    val distanceFromCenter = distanceTo(centralNode == localId)

    return when (distanceFromCenter) {
        in 0.0..1.0 ->25
        in 1.0..4.0 -> 75
        in 4.0..7.0 -> 50
        in 7.0..10.0 -> 0
        else -> 85
    }
}

/**
 * Elects multiple leaders in the network using bounded election.
 * Each node:
 * - computes its distance (in hops) to the leader of its group.
 * - belongs to a group whose leader is at most 5 hops away.
 */
fun Aggregate<Int>.multiLeader() = distanceTo(boundedElection(5) == localId)

/**
 * Calculating the *distance* from a node to a [sourceIDs] using a hop-gradient.
 */
fun Aggregate<Int>.distanceToSource(sourceIDs: List<Int>): SourceDistance =
    hopGradientCast(
        source = sourceIDs.contains(localId),
        local = SourceDistance(localId, 0),
        accumulateData = { _, _, value ->
            SourceDistance(value.sourceID, value.distance + 1)
        }
    )