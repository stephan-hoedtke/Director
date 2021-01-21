package com.stho.director

import kotlin.math.IEEErem

/**
 * Created by shoedtke on 20.01.2017.
 */
object Radian {

    fun fromDegrees(angleInDegree: Double): Double =
        Math.toRadians(angleInDegree)

    fun toDegrees(angleInRadian: Double): Double =
        Math.toDegrees(angleInRadian)

    fun fromHour(hour: Double): Double =
        hour * HOUR_IN_RADIAN

    fun normalize(radian: Double): Double =
        radian.IEEErem(TWO_PI).let {
            when {
                it < 0 -> it + TWO_PI
                else -> it
            }
        }

    fun normalizePi(radian: Double): Double =
        radian.IEEErem(TWO_PI).let {
            when {
                it > PI -> it - TWO_PI
                it < -PI -> it + TWO_PI
                else -> it
            }
        }

    private const val PI = Math.PI
    private const val TWO_PI = 2 * Math.PI
    private const val HOUR_IN_RADIAN = Math.PI / 12
}