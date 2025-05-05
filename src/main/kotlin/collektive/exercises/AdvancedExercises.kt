package collektive.exercises

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.collektive.alchemist.device.sensors.EnvironmentVariables
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.stdlib.spreading.hopGradientCast
import it.unibo.collektive.stdlib.fields.maxValue
import collektive.exercises.SourceDistance

/**
 * Consider the [source] identified in exercise 1, determine nodes 3 [hops] away from the [source].
 */
const val maxHops = 2
fun Aggregate<Int>.determineNodesInRange(environment: EnvironmentVariables): Int = TODO()

/**
 * Defined a data class to represent the association between a [source] node and its [distance].
*/ 
data class SourceDistance(val sourceID: Int, val distance: Int)

/**
 * Calculating the [distance] from a node to a [given source].
 */
fun Aggregate<Int>.distanceToSource(sourceID: List<Int>): SourceDistance = TODO()
    
/**
 * Determine the number of hops towards the [nearest source].
*/
fun Aggregate<Int>.nearestSource(environment: EnvironmentVariables): SourceDistance {
    val sourceID: List<Int> = environment["sources"]
    environment["isSource"] = sourceID.contains(localId)
    return distanceToSource(sourceID)
}

/**
 * Determine the number of [hops] towards the nearest [source] in the neighborhood.
*/
fun Aggregate<Int>.distanceFurthestNodeToSource(environment: EnvironmentVariables): Int = TODO()