package collektive.exercises


import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.stdlib.doubles.FieldedDoubles.plus
import it.unibo.collektive.stdlib.fields.minValue
import kotlin.Double.Companion.POSITIVE_INFINITY

/**
 * Bellman-Ford adaptive gradient
 */
fun <ID: Any> Aggregate<ID>.distanceTo(source: Boolean, metric: Field<ID, Double>) = share(POSITIVE_INFINITY) { distances ->
    val throughNeighbor = (distances + metric).minValue() ?: POSITIVE_INFINITY
    if (source) 0.0 else throughNeighbor
}
