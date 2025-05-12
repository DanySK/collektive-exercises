package collektive.exercises

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate

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
    val distanceFromCenter = 0.0

    // Function use to render the values and the colours of nodes
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
fun Aggregate<Int>.multiLeader(): Int = 0

/**
 * Calculating the *distance* from a node to a [sourceIDs] using a hop-gradient.
 */
fun Aggregate<Int>.distanceToSource(sourceIDs: List<Int>): SourceDistance =
    SourceDistance(localId, 0)