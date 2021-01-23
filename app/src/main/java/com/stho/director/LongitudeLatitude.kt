package com.stho.director

import android.location.Location
import kotlin.math.abs

class LongitudeLatitude(val longitude: Double, val latitude: Double) {

    override fun toString(): String =
        "${Angle.toString(longitude, Angle.AngleType.LONGITUDE)} ${Angle.toString(latitude, Angle.AngleType.LATITUDE)}"

    fun isNear(otherLocation: Location) =
        abs(Degree.difference(latitude, otherLocation.latitude)) < EPS && abs(Degree.difference(longitude, otherLocation.longitude)) < EPS

    companion object {
        private const val EPS: Double = 0.0001

        internal fun fromLocation(location: Location): LongitudeLatitude =
            LongitudeLatitude(longitude = location.longitude, latitude = location.latitude)

        // Berlin: longitude = 13.4925, latitude = 52.6425
        internal val defaultBerlin: LongitudeLatitude =
            LongitudeLatitude(longitude = 13.1111, latitude = 52.1111)
    }
}