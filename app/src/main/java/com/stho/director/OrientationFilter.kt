package com.stho.director

import com.stho.nyota.Acceleration


interface OrientationFilter {
    fun onOrientationAnglesChanged(orientationAngles: FloatArray)
    val currentOrientation: Orientation
    val updateCounter: Long
}

/*
    The class takes updates of the orientation vector by listening to onOrientationAnglesChanged(angles).
    The values will be stored and smoothed with acceleration.
    A handler will regularly read the updated smoothed orientation
 */
class OrientationAccelerationFilter: OrientationFilter {

    override var updateCounter: Long = 0L
        private set

    private val azimuthAcceleration: Acceleration = Acceleration(0.5)
    private val pitchAcceleration: Acceleration = Acceleration(0.5)
    private val rollAcceleration: Acceleration = Acceleration(0.5)
    private val lowPassFilter: LowPassFilter = LowPassFilter()

    override val currentOrientation: Orientation
        get() {
            val azimuth = azimuthAcceleration.position
            val pitch = pitchAcceleration.position
            val roll = rollAcceleration.position
            val direction = pitch - 90
            return Orientation(
                azimuth = Degree.normalize(azimuth),
                pitch = Degree.normalizeTo180(pitch),
                direction = Degree.normalizeTo180(direction),
                roll =  Degree.normalizeTo180(roll),
                lowPassFilter.getVector()
            )
        }

    override fun onOrientationAnglesChanged(orientationAngles: FloatArray) {
        val vector: Vector = lowPassFilter.setAcceleration(orientationAngles)

        val roll = Radian.toDegrees(vector.z)
        val pitch = -Radian.toDegrees(vector.y)
        val azimuth = Radian.toDegrees(vector.x)

        rollAcceleration.rotateTo(roll)
        pitchAcceleration.rotateTo(pitch)
        azimuthAcceleration.rotateTo(azimuth)

        updateCounter++
    }
}

