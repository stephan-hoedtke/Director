package com.stho.director

class Star(var rightAscension: Double, var declination: Double) {

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

    companion object {
        internal val default: Star =
            Star(Algorithms.parseHourString("2h 31m 49"), Algorithms.parseAngleString("+89° 15' 51"))
    }
}
