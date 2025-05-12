package collektive.exercises

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.stdlib.consensus.boundedElection
import it.unibo.collektive.stdlib.spreading.distanceTo
import it.unibo.collektive.stdlib.spreading.gossipMax
import it.unibo.collektive.stdlib.spreading.gossipMin
import it.unibo.collektive.stdlib.spreading.hopGradientCast
import it.unibo.collektive.stdlib.spreading.intGradientCast
import it.unibo.collektive.stdlib.util.hops
import kotlin.math.abs
import kotlin.math.hypot

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
    val distToRandom = distanceTo(gossipMin(localId) == localId, metric = metric)
    val firstExtreme = gossipMax(distToRandom to localId, compareBy { it.first }).second
    val distanceToExtreme = distanceTo(firstExtreme == localId, metric = metric)
    val (distanceBetweenExtremes, otherExtreme) =
        gossipMax(distanceToExtreme to localId, compareBy { it.first })
    val distanceToOtherExtreme = distanceTo(otherExtreme == localId, metric = metric)
    val distanceFromOpposedDiagonal = abs(distanceToExtreme - distanceToOtherExtreme)
    val distanceFromMainDiameter = abs(distanceBetweenExtremes - distanceToExtreme - distanceToOtherExtreme)
    val approximateDistance = hypot(distanceFromOpposedDiagonal, distanceFromMainDiameter)
    val centralNode = gossipMin(approximateDistance to localId, compareBy { it.first }).second
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
 * Elects multiple leaders in the network using bounded election, forming groups of up to 5 nodes.
 * Each node computes its distance (in hops) to the leader of its group.
 */
//fun Aggregate<Int>.multiLeader() = distanceTo(boundedElection(5) == localId)
fun Aggregate<Int>.multiLeader() = intGradientCast(
    boundedElection(5) == localId,
    localId,
    hops()
)

/**
 * Defined a data class to represent the association between a [sourceID] node and its [distance].
*/ 
data class SourceDistance(val sourceID: Int, val distance: Int)

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