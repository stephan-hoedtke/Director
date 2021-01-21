package com.stho.nyota

import com.stho.director.Degree
import com.stho.director.SystemClockTimeSource
import com.stho.director.TimeSource

internal class Acceleration(factorInSeconds: Double = 0.8, private val timeSource: TimeSource = SystemClockTimeSource()) {

/*
    // s(t) = a * t^3 + b * t^2 + c * t + d
    // s(0) = s0
    // s(1) = s1
    // s'(0) = v0
    // s'(1) = 0
    // -->  a = v0 - 2 (s1 - s0)
    //      b = 3 (s1 - s0) - 2 v0
    //      c = v0
    //      d = s0
  */

    private var v0: Double = 0.0
    private var s0: Double = 0.0
    private var s1: Double = 0.0
    private var a = 0.0
    private var b = 0.0
    private var c = 0.0
    private var d = 0.0
    private var t0: Double = timeSource.elapsedRealtimeSeconds
    private val factor = 1 / factorInSeconds

    init {
        calculateFormula()
    }

    val position: Double
        get() {
            val t = getTime(timeSource.elapsedRealtimeSeconds)
            return getPosition(t)
        }

    fun updateLinearTo(targetPosition: Double) {
        val t1 = timeSource.elapsedRealtimeSeconds
        val t = getTime(t1)
        val v = getSpeed(t)
        val s = getPosition(t)
        v0 = v
        s0 = s
        s1 = targetPosition
        t0 = t1
        calculateFormula()
    }

    fun rotateTo(targetAngle: Double) {
        // current 10°, new target = 30° --> o.k.
        // current 350°, new target = 330° --> o.k.
        // current 10°, new target = 350° --> rotate in negative direction to -20
        // current 350°, new target = 10° --> rotate in positive direction to 370
        // the trick:
        //      instead of 350° to 10° we will move from -10° to 10°
        //      instead of 10° to 350° we will move from 10° to -20°

        val t1 = timeSource.elapsedRealtimeSeconds
        val t = getTime(t1)
        val v = getSpeed(t)
        val s = getPosition(t)
        val angle = Degree.difference(targetAngle, s)
        v0 = v
        s0 = s
        s1 = s + angle
        t0 = t1

        when {
            angle > 0 && s1 > 360 -> {
                s0 -= 360
                s1 -= 360
            }
            angle < 0 && s1 < 0 -> {
                s0 += 360
                s1 += 360
            }
        }

        calculateFormula()
    }

    private fun getTime(t1: Double): Double =
        factor * (t1 - t0)

    private fun calculateFormula() {
        a = v0 - 2 * (s1 - s0)
        b = 3 * (s1 - s0) - 2 * v0
        c = v0
        d = s0
    }

    private fun getPosition(t: Double): Double =
        when {
            t < 0 -> s0
            t > 1 -> s1
            else -> ((a * t + b) * t + c) * t + d
        }

    private fun getSpeed(t: Double): Double =
        when {
            t < 0 -> v0
            t > 1 -> 0.0
            else -> (3 * a * t + 2 * b) * t + c
        }
}

