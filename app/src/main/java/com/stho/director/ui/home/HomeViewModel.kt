package com.stho.director.ui.home

import androidx.lifecycle.LiveData
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

    val starVectorLD: LiveData<Vector>
        get() = repository.starVectorLD

}
