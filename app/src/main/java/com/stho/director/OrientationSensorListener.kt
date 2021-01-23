package com.stho.director

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Display
import android.view.Surface
import android.view.WindowManager

interface IOrientationFilter {
    fun onOrientationChanged(R: FloatArray)
}

class OrientationSensorListener(private val context: Context, private val filter: IOrientationFilter) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val windowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val rotationMatrixAdjusted = FloatArray(9)
    private var display: Display? = null

    internal fun onResume() {
        display = windowManager.defaultDisplay
        initializeAccelerationSensor()
        initializeMagneticFieldSensor()
    }

    internal fun onPause() {
        display = null
        removeSensorListeners()
    }

    private fun initializeAccelerationSensor() {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    private fun initializeMagneticFieldSensor() {
        val magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    private fun removeSensorListeners() =
        sensorManager.unregisterListener(this)

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // We don't care
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
                updateOrientation()
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
                updateOrientation()
            }
        }
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    private fun updateOrientation() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)) {
            val adjustedRotationMatrix = getAdjustedRotationMatrix()
            filter.onOrientationChanged(adjustedRotationMatrix)
        }
    }

    /*
      See the following training materials from google.
      https://codelabs.developers.google.com/codelabs/advanced-android-training-sensor-orientation/index.html?index=..%2F..advanced-android-training#0
     */
    private fun getAdjustedRotationMatrix(): FloatArray {
        when (display?.rotation) {
            Surface.ROTATION_90 -> {
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, rotationMatrixAdjusted)
                return rotationMatrixAdjusted
            }
            Surface.ROTATION_180 -> {
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, rotationMatrixAdjusted)
                return rotationMatrixAdjusted
            }
            Surface.ROTATION_270 -> {
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, rotationMatrixAdjusted)
                return rotationMatrixAdjusted
            }
            else -> {
                return rotationMatrix
            }
        }
    }
}