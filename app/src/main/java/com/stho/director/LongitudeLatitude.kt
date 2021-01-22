package com.stho.director

import android.location.Location

class LongitudeLatitude(val longitude: Double, val latitude: Double) {

    override fun toString(): String =
        "${Angle.toString(longitude, Angle.AngleType.LONGITUDE)} ${Angle.toString(latitude, Angle.AngleType.LATITUDE)}"

    companion object {
        internal fun fromLocation(location: Location): LongitudeLatitude =
            LongitudeLatitude(longitude = location.longitude, latitude = location.latitude)

        internal val defaultBerlin: LongitudeLatitude =
            LongitudeLatitude(longitude = 13.4925, latitude = 52.6425)
    }
}