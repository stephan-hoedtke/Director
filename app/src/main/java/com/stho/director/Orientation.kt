package com.stho.director

import android.view.Gravity

/*******************************************************************************************************
    Orientation:
    - is the direction in which your eyes look into the phone.
    - orthogonal to the phones surface, going out from the back.
    - Z-axis of the phone

    Pitch:
    - is the direction from bottom to top of the phone
    - parallel to the phones surface, going out on the top.
    - Y-axis of the phone
    --> [-90 -- +90] positive: to the ground (it's not [-180 -- +180])

    Azimuth:
    - is the angle between Pitch (or the orientation) and the geographic north at the horizon plane.

    Roll:
    - [-180 -- +180] positive: to the left (it's not [-90 -- +90])
 *****************************************************************************************************/
/*
    Orientation: azimuth, pitch and roll in Degree
 */
data class Orientation(val azimuth: Double, val pitch: Double, val direction: Double, val roll: Double, val gravity: IVector) {

    fun getRotationToTargetAt(targetAzimuth: Double): Float =
        Degree.difference(x = targetAzimuth, y = azimuth).toFloat()

    fun getRotationToNorth(): Float =
        Degree.normalizeTo180(-azimuth).toFloat()

    companion object {
        val defaultOrientation: Orientation
            get() = Orientation(0.0, 0.0, -90.0, 0.0, Vector())
    }
}

