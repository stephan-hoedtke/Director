package com.stho.director

import com.stho.nyota.Acceleration
import kotlin.math.sqrt


interface OrientationFilter {
    fun onOrientationAnglesChanged(rotationMatrix: FloatArray, orientationAngles: FloatArray)
    val currentOrientation: Orientation
    val updateCounter: Long
    fun rotateFromEarthToPhone(earth: Vector): Vector
    fun rotateFromPhoneToEarth(phone: Vector): Vector
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
    private var rotationMatrix: FloatArray? = null

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

    override fun onOrientationAnglesChanged(rotationMatrix: FloatArray, orientationAngles: FloatArray) {
        this.rotationMatrix = rotationMatrix
        val vector: Vector = lowPassFilter.setAcceleration(orientationAngles)

        val roll = Radian.toDegrees(vector.z)
        val pitch = -Radian.toDegrees(vector.y)
        val azimuth = Radian.toDegrees(vector.x)

        rollAcceleration.rotateTo(roll)
        pitchAcceleration.rotateTo(pitch)
        azimuthAcceleration.rotateTo(azimuth)

        updateCounter++
    }

    /**
     * rotate with inverse rotation matrix: inverse = transpose
     */
    override fun rotateFromEarthToPhone(earth: Vector): Vector =
        rotationMatrix?.let {
            val x = it[0] * earth.x + it[3] * earth.y + it[6] * earth.z
            val y = it[1] * earth.x + it[4] * earth.y + it[7] * earth.z
            val z = it[2] * earth.x + it[5] * earth.y + it[8] * earth.z
            val f = 1 / sqrt(x * x + y * y + z * z)
            Vector(x = f * x, y = f * y, z = f * z)
        } ?: earth

    /**
     * rotate with rotation matrix
     */
    override fun rotateFromPhoneToEarth(phone: Vector): Vector =
        rotationMatrix?.let {
            val x = it[0] * phone.x + it[1] * phone.y + it[2] * phone.z
            val y = it[3] * phone.x + it[4] * phone.y + it[5] * phone.z
            val z = it[6] * phone.x + it[7] * phone.y + it[8] * phone.z
            val f = 1 / sqrt(x * x + y * y + z * z)
            Vector(x = f * x, y = f * y, z = f * z)
        } ?: phone
}



