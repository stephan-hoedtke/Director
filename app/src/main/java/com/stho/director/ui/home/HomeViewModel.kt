package com.stho.director.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stho.director.Orientation
import com.stho.director.Repository

class HomeViewModel : ViewModel() {

    private val repository: Repository = Repository.getInstance()

    val orientationLD: LiveData<Orientation>
        get() = repository.orientationLD
}
