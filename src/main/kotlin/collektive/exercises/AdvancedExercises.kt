package collektive.exercises

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.collektive.alchemist.device.sensors.EnvironmentVariables
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.stdlib.spreading.hopGradientCast
import it.unibo.collektive.stdlib.fields.maxBy
import collektive.exercises.SourceDistance

/**
 * Consider the source identified in exercise 1, determine nodes 3 hops away from the source.
 */
const val maxHops = 2
fun Aggregate<Int>.determineNodesInRange(environment: EnvironmentVariables): Int{
    val isSource = searchSource(environment)
    return hopGradientCast(
        source = isSource,
        local = localId,
        accumulateData = { fromSource, _, value ->
            if (fromSource > maxHops) {
                Int.MAX_VALUE
            } else {
                value
            }
        }
    )
}

/**
 * Defined a data class to represent the association between a source node and its distance.
*/ 
data class SourceDistance(val sourceID: Int, val distance: Int)

/**
 * Calculating the distance from a node to a given source.
 */
fun Aggregate<Int>.distanceToSource(sourceID: List<Int>) =  
    hopGradientCast(
        source = sourceID.contains(localId),
        local = SourceDistance(localId, 0),
        accumulateData = { _, _, value ->
            SourceDistance(value.sourceID, value.distance + 1)
        }
    )
    
/**
 * Determine the number of hops towards the nearest source.
*/
fun Aggregate<Int>.nearestSource(environment: EnvironmentVariables): SourceDistance {
    val sourceID: List<Int> = environment["sources"]
    environment["isSource"] = sourceID.contains(localId)
    return distanceToSource(sourceID)
}

/**
 * Determine the number of hops towards the nearest source.
*/
fun Aggregate<Int>.furthestNodeToSource(environment: EnvironmentVariables): Int? {
    val nearestSource = nearestSource(environment)
    environment["nearestSource"] = nearestSource
    return share(nearestSource){ field ->
        val values = field.neighborsValues + field.local.value
        values.maxBy {
            if(it.sourceID == field.local.value.sourceID){
                it.distance
            }else{
                Int.MIN_VALUE
            } 
        }
    }.also{
        environment["isFurthest"] = it.distance == nearestSource.distance 
    }.distance
}