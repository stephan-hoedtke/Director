package com.stho.director

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
data class Orientation(val pointerAzimuth: Double, val pointerAltitude: Double, val roll: Double, val centerAzimuth: Double, val centerAltitude: Double) {

    companion object {
        val defaultOrientation: Orientation
            get() = Orientation(0.0, 0.0, -10.0, 0.0, centerAltitude = -90.0)
    }
}

