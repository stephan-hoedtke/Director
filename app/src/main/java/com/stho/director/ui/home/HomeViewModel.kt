package com.stho.director.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stho.director.*

class HomeViewModel : ViewModel() {

    private val repository: Repository = Repository.getInstance()

    val orientationLD: LiveData<Orientation>
        get() = repository.orientationLD

    val northVectorLD: LiveData<Vector>
        get() = repository.northVectorLD

    val gravityVectorLD: LiveData<Vector>
        get() = repository.gravityVectorLD

    val starLD: LiveData<Star>
        get() = repository.starLD

    val centerLD: LiveData<AzimuthAltitude>
        get() = repository.centerLD

    val pointerLD: LiveData<AzimuthAltitude>
        get() = repository.pointerLD
}
