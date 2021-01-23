package com.stho.director.ui.settings

import androidx.lifecycle.ViewModel
import com.stho.director.Repository

class SettingsViewModel : ViewModel() {

    val repository: Repository = Repository.getInstance()

}