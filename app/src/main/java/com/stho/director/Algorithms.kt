
package com.stho.director

import android.location.Location
import java.security.InvalidParameterException
import java.util.*
import java.util.regex.Pattern
import kotlin.math.IEEErem
import kotlin.math.abs
import kotlin.math.sign

@Suppress("PrivatePropertyName", "FunctionName", "SpellCheckingInspection", "LocalVariableName")
class Algorithms(private var longitude: Double = 0.0, private var latitude: Double = 0.0) {

    private val utc: Calendar
        get() = Calendar.getInstance(TimeZone.getTimeZone("GMT"))

    private val julianDay: Double
        get() = getJulianDayForGMT(utc)

    fun setLocation(location: LongitudeLatitude) {
        longitude = location.longitude
        latitude = location.latitude
    }

    fun getAzimuthAltitudeFor(RA: Double, Decl: Double): AzimuthAltitude {
        val HA = Degree.normalizeTo180(15 * LST - RA) // Hour Angle (HA) is usually given in the interval -12 to +12 angleInHours, or -180 to +180 degrees
        val x = Degree.cos(HA) * Degree.cos(Decl)
        val y = Degree.sin(HA) * Degree.cos(Decl)
        val z = Degree.sin(Decl)
        val xhor = x * Degree.sin(latitude) - z * Degree.cos(latitude)
        val zhor = x * Degree.cos(latitude) + z * Degree.sin(latitude)
        val azimuth = Degree.arcTan2(y, xhor) + 180 // measure from north eastward
        val altitude = Degree.arcTan2(zhor, Math.sqrt(xhor * xhor + y * y))
        return AzimuthAltitude(azimuth, altitude)
    }

    private val LST
        get() = LST(julianDay, longitude)

    companion object {

        private val hourPattern = Pattern.compile("^(\\d+)[h]\\s(\\d+)[m]\\s(\\d+[.]*\\d*)$") // for:  13h 25m 11.60s

        internal fun parseHourString(str: String): Double {
            val m = hourPattern.matcher(str)
            if (m.find() && m.groupCount() == 3) {
                val hour = m.group(1)!!.toInt()
                val minute = m.group(2)!!.toInt()
                val seconds = m.group(3)!!.toDouble()
                return getHourAngle(hour, minute, seconds)
            }
            throw InvalidParameterException("Invalid hour $str")
        }

        private fun getHourAngle(hour: Int, minute: Int, seconds: Double): Double =
                hour + minute / 60.0 + seconds / 3600.0

        internal fun hourToString(angleInHours: Double): String {
            val hours: Double = normalizeHour(angleInHours)
            val hour: Int = hours.toInt()
            val minutes = 60 * (hours - hour)
            val minute = minutes.toInt()
            val seconds = 60 * (minutes - minute)
            return hour.toString() + "h " + minute + "m " + Formatter.df0.format(seconds) + "s"
        }

        private val anglePattern = Pattern.compile("^([+|−|-|–|-])(\\d+)[°]\\s(\\d+)[′|']\\s(\\d+[.]*\\d*)$") // for:  −11° 09′ 40.5

        fun parseAngleString(str: String): Double {
            val m = anglePattern.matcher(str)
            if (m.find() && m.groupCount() == 4) {
                val degree: Int= m.group(2)?.toInt() ?: 0
                val minute: Int= m.group(3)?.toInt() ?: 0
                val seconds: Double = m.group(4)?.toDouble() ?: 0.0
                return if (m.group(1) == "+") {
                    getAngle(+1, degree, minute, seconds)
                } else {
                    getAngle(-1, degree, minute, seconds)
                }
            }
            throw InvalidParameterException("Invalid degree $str")
        }

        private fun getAngle(sign: Int, degree: Int, minute: Int, seconds: Double) =
            sign * (degree + minute / 60.0 + seconds / 3600.0)


        /**
         * Local Sidereal Time = Greenwich Mean Siderial Time + Longitude = The time based on the rotation of the earth in relation to the stars (vernal equinox crosses local meridian).
         * @param julianDay Julian Day
         * @param longitude Observer's longitude in degree
         * @returns [L]ocal [S]idereal [T]ime in hours
         */
        private fun LST(julianDay: Double, longitude: Double): Double =
                normalizeHour(GMST(julianDay) + longitude / 15)

        /**
         * Julian Day Number
         * @param utc a date in UTC
         * @returns Julian Day Number
         * see for example: https://squarewidget.com/julian-day/ by James Still
         */
        fun getJulianDayForGMT(utc: Calendar): Double {
            if (BuildConfig.DEBUG) {
                if (utc.timeZone.rawOffset != 0) {
                    throw InvalidParameterException("Julian day requires GMT")
                }
            }
            var Y: Int = utc[Calendar.YEAR]
            var M: Int = utc[Calendar.MONTH] + 1
            val D: Int = utc[Calendar.DAY_OF_MONTH]
            if (M == 1 || M == 2) {
                Y -= 1
                M += 12
            }
            val A: Int = Y / 100
            val B = if (isGregorian(Y, M, D)) 2 - A + (A / 4) else 0
            val JD = Algorithms.truncate(365.25 * (Y + 4716)) + Algorithms.truncate(30.6001 * (M + 1)) + D + B - 1524.5
            val UT = Algorithms.UT(utc)
            return JD + UT / 24.0
        }

        /**
         * Universal Time in angleInHours (without year / month / day)
         * @param utc a date in UTC
         * @returns Universal Time
         */
        private fun UT(utc: Calendar): Double {
            val H = utc[Calendar.HOUR_OF_DAY]
            val M = utc[Calendar.MINUTE]
            val S = utc[Calendar.SECOND]
            val X = utc[Calendar.MILLISECOND]
            return H + M / 60.0 + S / 3600.0 + X / 3600000.0
        }

        private fun isGregorian(Y: Int, M: Int, D: Int): Boolean =
            Y > 1582 || Y == 1582 && M > 10 || Y == 1582 && M == 10 && D > 4

        private fun truncate(value: Double): Double =
                kotlin.math.truncate(value)

        private fun decimals(value: Double): Double =
                value - truncate(value)

        private fun normalizeHour(hour: Double): Double =
                hour.IEEErem(24.0).let {
                    when {
                        it < 0 -> it + 24.0
                        else -> it
                    }
                }

        /**
         * Greenwich Mean Sideral Time, the Local Sidereal Time at Greenwich - The time based on the rotation of the earth in relation to the stars (vernal equinox crosses greenwich meridian).
         * @param julianDay Julian Day
         * @returns [G]reenwich [M]ean [S]ideral [T]ime in hours
         */
        private fun GMST(julianDay: Double): Double =
                GMST_RummelPeters(julianDay)

        /**
         * Following the explanation of Rummel and Peters
         * https://de.wikipedia.org/wiki/Sternzeit
         */
        private fun GMST_RummelPeters(julianDay: Double): Double {
            val omega = 1.00273790935
            val UT1 = decimals(julianDay - 0.5)
            val r = (julianDay - UT1 - 2451545.0) / 36525
            val s = 24110.54841 + r * (8640184.812866 + r * (0.093104 + r * 0.0000062))
            return normalizeHour(s / SECONDS_PER_HOUR + 24 * UT1 * omega)
        }

        private const val SECONDS_PER_HOUR = 3600.0
    }
}