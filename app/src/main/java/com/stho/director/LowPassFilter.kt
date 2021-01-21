package com.stho.director

/**
 * A low pass filter for a 3-vector of angles
 */
internal class LowPassFilter(private val timeConstant: Double = 0.2, private val timeSource: TimeSource = SystemClockTimeSource()) {

    private val vector: Vector = Vector(0.0, 0.0, 9.78)
    private var startTime: Double = 0.0

    fun setAcceleration(acceleration: FloatArray): Vector {
        lowPassFilter(acceleration)
        return vector
    }

    fun getVector(): IVector =
        vector

    fun reset() {
        vector.x = 0.0
        vector.y = 0.0
        vector.z = 9.78
    }

    private fun lowPassFilter(acceleration: FloatArray) {
        val x = Radian.normalizePi(acceleration[0].toDouble())
        val y = Radian.normalizePi(acceleration[1].toDouble())
        val z = Radian.normalizePi(acceleration[2].toDouble())

        if (lookAtPhoneFromAbove(roll = z)) {
            lowPassFilter(x, y, z)
        } else {
            lowPassFilterForLookingAtThePhoneFromBelow(x, y, z)
        }
    }

    private fun lookAtPhoneFromAbove(roll: Double) =
        -PI_2 < roll && roll < PI_2

    private fun lowPassFilterForLookingAtThePhoneFromBelow(x: Double, y: Double, z: Double) =
        lowPassFilter(x = Radian.normalizePi( PI - x), y = Radian.normalizePi( PI - y), z = Radian.normalizePi(
            PI + z))

    private fun lowPassFilter(x: Double, y: Double, z: Double) {
        val dt: Double = getTimeDifferenceInSeconds()
        if (dt > 0) {
            val alpha = getAlpha(dt)
            vector.x = Radian.normalizePi(vector.x + alpha * Radian.normalizePi(x - vector.x))
            vector.y = Radian.normalizePi(vector.y + alpha * Radian.normalizePi(y - vector.y))
            vector.z = Radian.normalizePi(vector.z + alpha * Radian.normalizePi(z - vector.z))
        } else {
            vector.x = x
            vector.y = y
            vector.z = z
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

    companion object {
        private const val PI = Math.PI
        private const val PI_2 = 0.5 * Math.PI
    }
}


