package collektive.exercises

import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.alchemist.model.positions.Euclidean2DPosition
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring

fun Aggregate<Int>.metricDistance(simulatedDevice: CollektiveDevice<Euclidean2DPosition>): Field<Int, Double> {
    val localPosition = simulatedDevice.environment.getPosition(simulatedDevice.node)
    return neighboring(localPosition).mapValues {
        localPosition.distanceTo(it)
    }
}

fun Aggregate<Int>.entrypoint(simulatedDevice: CollektiveDevice<Euclidean2DPosition>): Double {
    val programOutput = distanceTo(simulatedDevice.localId == 0, metricDistance(simulatedDevice))
    // format the result to three decimals
    val formattedResult = String.format("%.3f", programOutput).toDouble()
    return formattedResult
}
