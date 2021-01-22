package com.stho.director

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.math.sqrt

class Repository {

    private val orientationLiveData = MutableLiveData<Orientation>().apply { value = Orientation.defaultOrientation }
    private val northVectorLiveData = MutableLiveData<Vector>().apply { value = Vector() }
    private val gravityVectorLiveData = MutableLiveData<Vector>().apply { value = Vector() }
    private val starLiveData = MutableLiveData<Star>().apply { value = Star.default } // Orion at Jan 21st 2021 at 22:40 CET from Berlin
    private val centerLiveData = MutableLiveData<AzimuthAltitude>().apply { value = AzimuthAltitude() }
    private val pointerLiveData = MutableLiveData<AzimuthAltitude>().apply { value = AzimuthAltitude() }
    private val locationLiveData = MutableLiveData<LongitudeLatitude>().apply { value = LongitudeLatitude.defaultBerlin }

    val settings = Settings()

    val orientationLD: LiveData<Orientation>
        get() = orientationLiveData

    internal fun updateOrientation(orientation: Orientation) {
        orientationLiveData.postValue(orientation)
    }

    val locationLD: LiveData<LongitudeLatitude>
        get() = locationLiveData

    internal fun updateLocation(location: Location) {
        locationLiveData.postValue(LongitudeLatitude.fromLocation(location))
    }

    val northVectorLD: LiveData<Vector>
        get() = northVectorLiveData

    internal fun updateNorthVector(vector: Vector) {
        northVectorLiveData.postValue(vector)
    }

    val gravityVectorLD: LiveData<Vector>
        get() = gravityVectorLiveData

    internal fun updateGravityVector(vector: Vector) {
        gravityVectorLiveData.postValue(vector)
    }

    val starLD: LiveData<Star>
        get() = starLiveData

    internal fun updateStar(star: Star) {
        starLiveData.postValue(star)
    }

    val star: Star
        get() = starLiveData.value ?:  Star.default

    val centerLD: LiveData<AzimuthAltitude>
        get() = centerLiveData

    internal fun updateCenter(center: Vector) {
        val l = sqrt(center.x * center.x + center.y * center.y)
        val azimuth = Degree.arcTan2(center.x, center.y)
        val altitude = Degree.arcTan2(center.z, l)
        centerLiveData.postValue(AzimuthAltitude(azimuth, altitude))
    }

    val pointerLD: LiveData<AzimuthAltitude>
        get() = pointerLiveData

    internal fun updatePointer(pointer: Vector) {
        val l = sqrt(pointer.x * pointer.x + pointer.y * pointer.y)
        val azimuth = Degree.arcTan2(pointer.x, pointer.y)
        val altitude = Degree.arcTan2(pointer.z, l)
        pointerLiveData.postValue(AzimuthAltitude(azimuth, altitude))
    }

    companion object {
        private val singleton: Repository by lazy {
            Repository()
        }

        fun getInstance(): Repository {
            return singleton
        }
    }
}