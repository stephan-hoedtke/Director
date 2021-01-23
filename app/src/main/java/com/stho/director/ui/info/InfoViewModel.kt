package com.stho.director.ui.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stho.director.*

class InfoViewModel : ViewModel() {

    private val repository: Repository = Repository.getInstance()

    val orientationLD: LiveData<Orientation>
        get() = repository.orientationLD

    val locationLD: LiveData<LongitudeLatitude>
        get() = repository.locationLD

    val northVectorLD: LiveData<Vector>
        get() = repository.northVectorLD

    val gravityVectorLD: LiveData<Vector>
        get() = repository.gravityVectorLD

    val starVectorLD: LiveData<Vector>
        get() = repository.starVectorLD

}