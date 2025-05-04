package collektive.exercises


import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.stdlib.doubles.FieldedDoubles.plus
import it.unibo.collektive.stdlib.fields.minValue
import kotlin.Double.Companion.POSITIVE_INFINITY as infinity

/**
 * Bellman-Ford adaptive gradient
 */
fun <ID: Any> Aggregate<ID>.distanceTo(source: Boolean, metric: Field<ID, Double>) = share(infinity) { distances ->
    val throughNeighbor = (distances + metric).minValue() ?: infinity
    if (source) 0.0 else throughNeighbor
}

/**
 * Utility class to store a distance and a value.
 * Better than a plain [Pair] because it is comparable and summable with doubles.
 */
data class DistanceValue<T>(val distance: Double, val value: T) : Comparable<DistanceValue<T>> {
    operator fun plus(distance: Double): DistanceValue<T> = DistanceValue(this.distance + distance, value)
    override fun compareTo(other: DistanceValue<T>): Int = distance.compareTo(other.distance)
    override fun toString(): String = "$value@$distance"
}

/**
 * Bellman-Ford-based hop-broadcast
 * (there are better options in the standard library).
 *
 * Type reification is necessary for serialization
 * (T must be serializable and known at compile time).
 */
inline fun <ID: Any, reified T> Aggregate<ID>.broadcast(source: Boolean, value: T): T {
    val top = DistanceValue(infinity, value)
    val myDistanceValue = share(top) { distancesToValues ->
        val closest = distancesToValues.minValue() ?: top
        if (source) DistanceValue(0.0, value) else closest + 1.0
    }
    return myDistanceValue.value
}

fun <ID: Any> Aggregate<ID>.distance(source: Boolean, destination: Boolean, metric: Field<ID, Double>) =
    broadcast(source, distanceTo(destination, metric))

fun <ID: Any> Aggregate<ID>.channel(
    source: Boolean,
    destination: Boolean,
    width: Double,
    metric: Field<ID, Double>,
): Boolean =
    distanceTo(source, metric) + distanceTo(destination, metric) < distance(source, destination, metric) + width

fun <ID: Any> Aggregate<ID>.channelAroundObstacles(
    isObstacle: Boolean,
    source: Boolean,
    destination: Boolean,
    width: Double,
    metric: Field<ID, Double>,
): Boolean = !isObstacle && channel(source, destination, width, metric)

