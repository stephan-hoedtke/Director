package com.stho.director

import com.stho.nyota.Acceleration


/*
    The class takes updates of the orientation vector by listening to onOrientationAnglesChanged(angles).
    The values will be stored and smoothed with acceleration.
    A handler will regularly read the updated smoothed orientation
 */
class OrientationAccelerationFilter: IOrientationFilter {

    private val pointerAzimuthAcceleration: Acceleration = Acceleration(0.5)
    private val pointerAltitudeAcceleration: Acceleration = Acceleration(0.5)
    private val rollAcceleration: Acceleration = Acceleration(0.5)
    private val centerAzimuthAcceleration: Acceleration = Acceleration(0.5)
    private val centerAltitudeAcceleration: Acceleration = Acceleration(0.5)
    private val lowPassFilter: LowPassFilterAnglesInDegree = LowPassFilterAnglesInDegree()
    private var rotationMatrix: FloatArray = FloatArray(9)

    val currentOrientation: Orientation
        get() {
            return Orientation(
                pointerAzimuth = Degree.normalize(pointerAzimuthAcceleration.position),
                pointerAltitude = Degree.normalizeTo180(pointerAltitudeAcceleration.position),
                roll =  Degree.normalizeTo180(rollAcceleration.position),
                centerAzimuth = Degree.normalize(centerAzimuthAcceleration.position),
                centerAltitude = Degree.normalizeTo180(centerAltitudeAcceleration.position),
            )
        }

    /**
     * to retrieve the orientation of the phone:
     *
     */

    override fun onOrientationChanged(R: FloatArray) {
        rotationMatrix = R

        /*
            1) azimuth and altitude: north vector = (0, 1, 0)
                R * north vector = (x = R[1], y = R[4], z = R[7])
                    azimuth = atan2(x, y) = atan2(R[1], R[4])
                    altitude = -asin(R[Z]) = asin(-R[7)

            2) roll: ???
                    roll = atan2(x, z) = atan2(R[6], R[8])

                see: SensorManager.getOrientation()
                    values[0] = (float) Math.atan2(R[1], R[4]);
                    values[1] = (float) Math.asin(-R[7]);
                    values[2] = (float) Math.atan2(-R[6], R[8]);

            3) center: center vector = (0, 0, -1)
                R * center vector = (x = -R[2], y = -R[5], z = -R[8])
                    azimuth = atan2(x, y) = atan2(-R[2], -R[5])
                    altitude = asin(z) = asin(-R[8])
        */

        val angles: DoubleArray = doubleArrayOf(
            Degree.arcTan2(R[1].toDouble(), R[4].toDouble()),
            Degree.arcSin(-R[7].toDouble()),
            Degree.arcTan2(-R[6].toDouble(), R[8].toDouble()),
            Degree.arcTan2(-R[2].toDouble(), -R[5].toDouble()),
            Degree.arcSin(-R[8].toDouble()))

        lowPassFilter.setAngles(angles)

        pointerAzimuthAcceleration.rotateTo(lowPassFilter.angles[0])
        pointerAltitudeAcceleration.rotateTo(lowPassFilter.angles[1])
        rollAcceleration.rotateTo(lowPassFilter.angles[2])
        centerAzimuthAcceleration.rotateTo(lowPassFilter.angles[3])
        centerAltitudeAcceleration.rotateTo(lowPassFilter.angles[4])
    }

}



