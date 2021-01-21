package com.stho.director

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Repository {

    private val orientationLiveData = MutableLiveData<Orientation>().apply { Orientation.defaultOrientation }

    val orientationLD: LiveData<Orientation>
        get() = orientationLiveData

    internal fun updateOrientation(orientation: Orientation) {
        orientationLiveData.postValue(orientation)
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