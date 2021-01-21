package com.stho.director

import kotlin.math.sqrt


interface IVector {
    val x: Double
    val y: Double
    val z: Double
}


/**
 * Created by shoedtke on 20.01.2017.
 */
class Vector(override var x: Double = 0.0, override var y: Double = 0.0, override var z: Double = 0.0) :
    IVector {

    val length: Double
        get() = sqrt(x * x + y * y + z * z)

    operator fun minus(v: Vector): Vector {
        return Vector(x - v.x, y - v.y, z - v.z)
    }

    val values: FloatArray
        get() = floatArrayOf(x.toFloat(), y.toFloat(), z.toFloat())
}

