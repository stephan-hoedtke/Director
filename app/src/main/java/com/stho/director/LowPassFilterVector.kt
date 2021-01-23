package com.stho.director

/**
 * A low pass filter for a 3-vector of values
 */
internal class LowPassFilterVector(private val timeConstant: Double = 0.1, private val timeSource: TimeSource = SystemClockTimeSource()) {

    private var startTime: Double = 0.0

    val values: Vector = Vector()

    /**
     * set the new angles in degrees
     */
    fun setValues(newValues: Vector) {
        val dt: Double = getTimeDifferenceInSeconds()
        if (dt > 0) {
            val alpha = getAlpha(dt)
            values.x = values.x + alpha * (newValues.x - values.x)
            values.y = values.y + alpha * (newValues.y - values.y)
            values.z = values.z + alpha * (newValues.z - values.z)
        } else {
            values.x = newValues.x
            values.y = newValues.y
            values.z = newValues.z
        }
    }

    private fun getTimeDifferenceInSeconds(): Double {
        val t0 = startTime
        val t1 = timeSource.elapsedRealtimeSeconds
        startTime = t1
        return t1 - t0
    }

    /**
     * dt > T --> 1.0, otherwise dt / T
     */
    private fun getAlpha(dt: Double): Double =
        dt.coerceAtMost(timeConstant) / timeConstant

}


