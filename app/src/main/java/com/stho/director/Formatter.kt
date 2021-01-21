package com.stho.director

import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by shoedtke on 04.10.2016.
 */
object Formatter {
    private val formatDateTimeSecTimeZone = SimpleDateFormat("d MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
    private val formatDateTimeSec = SimpleDateFormat("d MMM HH:mm:ss", Locale.ENGLISH)
    private val formatDateTimeTimeZone = SimpleDateFormat("d MMM yyyy HH:mm Z", Locale.ENGLISH)
    private val formatDateTime = SimpleDateFormat("d MMM HH:mm", Locale.ENGLISH)
    private val formatTime = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    private val formatTimeSec = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
    private val formatDateTimeZone = SimpleDateFormat("d MMM yyyy Z", Locale.ENGLISH)
    private val formatDate: SimpleDateFormat = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
    private val formatTimeZone = SimpleDateFormat("Z", Locale.ENGLISH)
    val df0: DecimalFormat = decimalFormat().apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
        minimumIntegerDigits = 1
        decimalFormatSymbols.groupingSeparator = UNICODE_THIN_SPACE
    }
    val df2: DecimalFormat = decimalFormat().apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
        minimumIntegerDigits = 1
    }
    val df3: DecimalFormat = decimalFormat().apply {
        minimumFractionDigits = 3
        maximumFractionDigits = 3
        minimumIntegerDigits = 1
    }
    internal const val SPACE = "  "
    private const val UNICODE_THIN_SPACE = '\u2009'
    internal val timeZoneGMT: TimeZone by lazy {
        TimeZone.getTimeZone("GMT")
    }

    fun toString(calendar: Calendar, timeFormat: TimeFormat): String =
        toString(calendar.time, calendar.timeZone, timeFormat)

    fun toString(value: Double): String =
        String.format(Locale.ENGLISH, "%.4f", value)

    fun toDistanceString(distance: Double): String =
        df0.format(distance) + " km"

    fun toSpeedString(speed: Double): String =
        df0.format(speed) + " km/h"

    private const val HALF_A_MINUTE_IN_MILLISECONDS: Long = 29000

    private fun toNearestWholeMinute(utc: Date): Date =
        Date(utc.time + HALF_A_MINUTE_IN_MILLISECONDS)

    fun toString(utc: Date, timeZone: TimeZone, timeFormat: TimeFormat): String =
        when (timeFormat) {
            TimeFormat.TIME -> {
                formatTime.timeZone = timeZone
                formatTime.format(utc)
            }
            TimeFormat.DATE -> {
                formatDate.timeZone = timeZone
                formatDate.format(utc)
            }
            TimeFormat.DATE_TIMEZONE -> {
                formatDateTimeZone.timeZone = timeZone
                formatDateTimeZone.format(utc)
            }
            TimeFormat.DATETIME -> {
                formatDateTime.timeZone = timeZone
                formatDateTime.format(toNearestWholeMinute(utc))
            }
            TimeFormat.DATETIME_TIMEZONE -> {
                formatDateTimeTimeZone.timeZone = timeZone
                formatDateTimeTimeZone.format(toNearestWholeMinute(utc))
            }
            TimeFormat.DATETIME_SEC -> {
                formatDateTimeSec.timeZone = timeZone
                formatDateTimeSec.format(utc)
            }
            TimeFormat.DATETIME_SEC_TIMEZONE -> {
                formatDateTimeSecTimeZone.timeZone = timeZone
                formatDateTimeSecTimeZone.format(utc)
            }
            TimeFormat.TIMEZONE -> {
                formatTimeZone.timeZone = timeZone
                formatTimeZone.format(utc)
            }
            TimeFormat.TIME_SEC -> {
                formatTimeSec.timeZone = timeZone
                formatTimeSec.format(utc)
            }
        }

    enum class TimeFormat {
        DATE, DATE_TIMEZONE, TIME, DATETIME, DATETIME_TIMEZONE, DATETIME_SEC, DATETIME_SEC_TIMEZONE, TIMEZONE, TIME_SEC
    }

    private fun decimalFormat(): DecimalFormat =
        DecimalFormat.getNumberInstance(Locale.ENGLISH) as DecimalFormat
 }

