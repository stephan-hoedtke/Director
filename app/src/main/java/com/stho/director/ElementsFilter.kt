package com.stho.director

import kotlin.math.sqrt

class ElementsFilter: IOrientationFilter {

    private val northVectorAcceleration = AccelerationVector()
    private val gravityAcceleration = AccelerationVector()
    private val starAcceleration = AccelerationVector()

    private val lowPassFilterNorthVector: LowPassFilterVector = LowPassFilterVector()
    private val lowPassFilterGravity: LowPassFilterVector = LowPassFilterVector()
    private val lowPassFilterStar: LowPassFilterVector = LowPassFilterVector()

    // (1, 0, 0) is pointing east
    // (0, 1, 0) is pointing north
    // (0, 0, 1) is pointing upwards into the sky

    private val north = Vector(0.0, 1.0, 0.0)
    private val gravity = Vector(0.0, 0.0, -1.0)
    private val star = Vector()

    fun setStar(earth: Vector) {
        star.x = earth.x
        star.y = earth.y
        star.z = earth.z
    }

    override fun onOrientationChanged(R: FloatArray) {
        lowPassFilterNorthVector.setValues(rotateFromEarthToPhone(R, north))
        lowPassFilterGravity.setValues(rotateFromEarthToPhone(R, gravity))
        lowPassFilterStar.setValues(rotateFromEarthToPhone(R, star))

        northVectorAcceleration.moveTo(lowPassFilterNorthVector.values)
        gravityAcceleration.moveTo(lowPassFilterGravity.values)
        starAcceleration.moveTo(lowPassFilterStar.values)
    }

    val currentNorthVector
        get() = northVectorAcceleration.position

    val currentGravity
        get() = gravityAcceleration.position

    val currentStar
        get() = starAcceleration.position

    companion object {

        /**
         * rotate with inverse rotation matrix: inverse = transpose
         */
        @Suppress("LiftReturnOrAssignment")
        fun rotateFromEarthToPhone(rotationMatrix: FloatArray, earth: Vector): Vector {
            val x = rotationMatrix[0] * earth.x + rotationMatrix[3] * earth.y + rotationMatrix[6] * earth.z
            val y = rotationMatrix[1] * earth.x + rotationMatrix[4] * earth.y + rotationMatrix[7] * earth.z
            val z = rotationMatrix[2] * earth.x + rotationMatrix[5] * earth.y + rotationMatrix[8] * earth.z
            val norm = x * x + y * y + z * z
            if (norm < 0.00000001)
                return earth
            else {
                val f = 1 / sqrt(norm)
                return Vector(x = f * x, y = f * y, z = f * z)
            }
        }

        /**
         * rotate with rotation matrix
         */
        fun rotateFromPhoneToEarth(rotationMatrix: FloatArray, phone: Vector): Vector {
            val x = rotationMatrix[0] * phone.x + rotationMatrix[1] * phone.y + rotationMatrix[2] * phone.z
            val y = rotationMatrix[3] * phone.x + rotationMatrix[4] * phone.y + rotationMatrix[5] * phone.z
            val z = rotationMatrix[6] * phone.x + rotationMatrix[7] * phone.y + rotationMatrix[8] * phone.z
            val f = 1 / sqrt(x * x + y * y + z * z)
            return Vector(x = f * x, y = f * y, z = f * z)
        }
    }
}