package com.stho.director

import com.stho.nyota.Acceleration

class AccelerationVector {
    private val x = Acceleration()
    private val y = Acceleration()
    private val z = Acceleration()

    fun moveTo(values: Vector) {
        x.updateLinearTo(values.x)
        y.updateLinearTo(values.y)
        z.updateLinearTo(values.z)
    }

    val position: Vector
        get() = Vector(x.position, y.position, z.position)
}