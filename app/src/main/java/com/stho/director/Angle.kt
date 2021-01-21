package com.stho.director

import kotlin.math.abs

object Angle {

    fun toString(angle: Double, angleType: AngleType?): String =
        when (angleType) {
            AngleType.AZIMUTH -> {
                val alpha = normalize(angle)
                Formatter.df0.format(abs(alpha)) + "° " + northEastSouthWest(alpha)
            }
            AngleType.ALTITUDE -> {
                val alpha = normalizeTo180(angle)
                sign(alpha) + Formatter.df0.format(abs(alpha)) + "°"
            }
            AngleType.LATITUDE -> {
                val alpha = normalizeTo180(angle)
                Formatter.df2.format(abs(alpha)) + "° " + northSouth(alpha)
            }
            AngleType.LONGITUDE -> {
                val alpha = normalizeTo180(angle)
                Formatter.df2.format(abs(alpha)) + "° " + eastWest(alpha)
            }
            AngleType.DEGREE_NORTH_EAST_SOUTH_WEST -> {
                val alpha = normalize(angle)
                Formatter.df0.format(abs(alpha)) + "° " + northEastSouthWest(alpha)
            }
            AngleType.PITCH -> {
                val alpha = normalizeTo180(angle)
                sign(alpha) + Formatter.df0.format(abs(alpha)) + "°"
            }
            AngleType.ROLL -> {
                val alpha = normalizeTo180(angle)
                sign(alpha) + Formatter.df0.format(abs(alpha)) + "°"
            }
            else -> {
                val alpha = normalizeTo180(angle)
                sign(alpha) + Formatter.df2.format(abs(alpha)) + "°"
            }
        }

    fun toString(azimuth: Double, altitude: Double, angleType: AngleType?): String =
        when (angleType) {
            AngleType.ORIENTATION -> {
                toString(azimuth, AngleType.AZIMUTH) + " " + toString(altitude, AngleType.ALTITUDE)
            }
            else -> {
                val alpha = normalizeTo180(azimuth)
                val beta = normalizeTo180(altitude)
                sign(alpha) + Formatter.df2.format(abs(alpha)) + "°" + sign(beta) + Formatter.df2.format(abs(beta)) + "°"
            }
        }


    private fun sign(x: Double): String =
        if (x < 0) "-" else "+"

    private fun northSouth(angle: Double) =
        if (angle >= 0) "N" else "S"

    private fun eastWest(angle: Double) =
        if (angle >= 0) "E" else "W"

    /*
        angle in [0*45 - 22.5 , 1*45 - 22.5] -> DIRECTION[0] = "N"
        angle in [1*45 - 22.5 , 2*45 - 22.5] -> DIRECTION[1] = "NE"
        angle in [2*45 - 22.5 , 3*45 - 22.5] -> DIRECTION[3] = "E"
        ...
     */
    private fun northEastSouthWest(angle: Double): String =
        DIRECTION[((angle + 22.5) / 45.0).toInt()]

    private fun normalize(angle: Double): Double =
        Degree.normalize(angle)

    private fun normalizeTo180(angle: Double): Double =
        Degree.normalizeTo180(angle)

    enum class AngleType {
        DEGREE_NORTH_EAST_SOUTH_WEST, AZIMUTH, ALTITUDE, LATITUDE, LONGITUDE, PITCH, ROLL, ORIENTATION
    }

    private val DIRECTION = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW", "N")

}