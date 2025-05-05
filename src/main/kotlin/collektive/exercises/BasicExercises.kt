package collektive.exercises

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.collektive.alchemist.device.sensors.EnvironmentVariables
import it.unibo.collektive.stdlib.spreading.hopDistanceTo
import it.unibo.collektive.stdlib.spreading.gradientCast
import it.unibo.collektive.stdlib.fields.minValue
import it.unibo.collektive.stdlib.fields.maxValue
import it.unibo.collektive.aggregate.api.share

/**
 * Select a node identified as [source], chosen by finding the node with [minimum uid] 
 * in the network.
*/
fun Aggregate<Int>.searchSource(environment: EnvironmentVariables): Boolean = TODO()

/**
 * Compute the [distances] between any node and the [source].
*/
fun Aggregate<Int>.distanceToSource(environment: EnvironmentVariables): Int = TODO()

/**
 * Calculate in the [source] an estimate of the true [diameter] of the network (the maximum distance of a device in the network).
 * Broadcast the [diameter] to every node in the network.
*/ 
fun Aggregate<Int>.networkDiameter(environment: EnvironmentVariables, distanceSensor: CollektiveDevice<*>): Int = TODO()

/**
 * Computes the [gradientCast] from the [source] with the [value] that is the distance from the [source] to the target.
 */
fun Aggregate<Int>.broadcast(distanceSensor: CollektiveDevice<*>, from: Boolean, payload: Double): Double = TODO()