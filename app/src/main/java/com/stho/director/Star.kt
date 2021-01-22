package com.stho.director

class Star(val rightAscension: Double, val declination: Double) {

    /**
     * azimuth and altitude if the star from the current location
     */
    var azimuthAltitude: AzimuthAltitude = AzimuthAltitude()

    /**
     * cartesian coordinates of the star from the current location parallel to the surface of the earth
     * @x: pointing east
     * @y: pointing north
     * @z: pointing upwards into the sky
     */
    val earth: Vector
        get() = Vector(
                x = Degree.sin(azimuthAltitude.azimuth),
                y = Degree.cos(azimuthAltitude.azimuth),
                z = Degree.sin(azimuthAltitude.altitude))


    /**
     * cartesian coordinates of the star from the phone: (assuming the phone is held upwards)
     * @x: pointing to the right of the phone
     * @y: pointing to the upper edge
     * @z: pointing to the eye of the user, perpendicular to the screen
     *
     * This position can be calculated using the rotation matrix R:
     *      @phone := R * @earth
     */
    var phone: Vector = Vector()

    companion object {
        internal val default: Star =
            Star(Algorithms.parseHourString("05h 36m 12.81"), Algorithms.parseAngleString("-01° 12′ 6.9"))
    }
}
