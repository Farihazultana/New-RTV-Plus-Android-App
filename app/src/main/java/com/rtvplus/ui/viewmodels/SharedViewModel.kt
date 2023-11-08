package com.rtvplus.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val flagLiveData = MutableLiveData<Boolean>()

}