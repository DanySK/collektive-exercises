package collektive.exercises

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.alchemist.device.sensors.EnvironmentVariables
import it.unibo.collektive.stdlib.consensus.boundedElection
import it.unibo.collektive.stdlib.fields.maxValue
import it.unibo.collektive.stdlib.spreading.distanceTo
import it.unibo.collektive.stdlib.spreading.gossipMax
import it.unibo.collektive.stdlib.spreading.gossipMin
import it.unibo.collektive.stdlib.spreading.gradientCast
import it.unibo.collektive.stdlib.spreading.hopGradientCast
import it.unibo.collektive.stdlib.spreading.intGradientCast
import it.unibo.collektive.stdlib.util.hops
import kotlin.math.abs
import kotlin.math.hypot

/**
 * Consider the [source] identified in exercise 1, determine nodes 3 [hops] away from the [source].
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

//fun Aggregate<Int>.multiLeader() = distanceTo(boundedElection(5) == localId)
fun Aggregate<Int>.multiLeader() = intGradientCast(
    boundedElection(5) == localId,
    localId,
    hops()
)

/**
 * Defined a data class to represent the association between a [source] node and its [distance].
*/ 
data class SourceDistance(val sourceID: Int, val distance: Int)

/**
 * Calculating the [distance] from a node to a [given source].
 */
fun Aggregate<Int>.distanceToSource(sourceID: List<Int>): SourceDistance =  
    hopGradientCast(
        source = sourceID.contains(localId),
        local = SourceDistance(localId, 0),
        accumulateData = { _, _, value ->
            SourceDistance(value.sourceID, value.distance + 1)
        }
    )
    
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
fun Aggregate<Int>.distanceFurthestNodeToSource(environment: EnvironmentVariables): Int {
    val nearestSource = nearestSource(environment)
    return neighboring(nearestSource).mapValues { 
        if(it.sourceID == nearestSource.sourceID){
            it.distance
        }else{
            Int.MIN_VALUE
        }
    }.maxValue(nearestSource.distance)
}