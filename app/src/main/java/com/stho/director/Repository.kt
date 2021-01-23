package com.stho.director

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.math.sqrt

class Repository {

    private val orientationLiveData = MutableLiveData<Orientation>().apply { value = Orientation.defaultOrientation }
    private val northVectorLiveData = MutableLiveData<Vector>().apply { value = Vector() }
    private val gravityVectorLiveData = MutableLiveData<Vector>().apply { value = Vector() }
    private val starVectorLiveData = MutableLiveData<Vector>().apply { value = Vector() } // Orion at Jan 21st 2021 at 22:40 CET from Berlin
    private val locationLiveData = MutableLiveData<LongitudeLatitude>().apply { value = LongitudeLatitude.defaultBerlin }

    val settings = Settings()

    val orientationLD: LiveData<Orientation>
        get() = orientationLiveData

    internal fun updateOrientation(orientation: Orientation) {
        orientationLiveData.postValue(orientation)
    }

    val locationLD: LiveData<LongitudeLatitude>
        get() = locationLiveData

    internal fun updateLocation(location: Location): Boolean {
        val value = locationLiveData.value ?: LongitudeLatitude.defaultBerlin
        return if (value.isNear(location))
            false
        else {
            locationLiveData.postValue(LongitudeLatitude.fromLocation(location))
            true
        }
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

    val starVectorLD: LiveData<Vector>
        get() = starVectorLiveData

    internal fun updateStarVector(vector: Vector) {
        starVectorLiveData.postValue(vector)
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