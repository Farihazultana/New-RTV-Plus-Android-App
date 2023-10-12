package com.rtvplus.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtvplus.data.models.registration.RegistrationResponse
import com.rtvplus.data.repository.RegistrationRepository
import com.rtvplus.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val repository: RegistrationRepository) : ViewModel(){
    private val _registrationData = MutableStateFlow<ResultType<RegistrationResponse>>(ResultType.Loading)
    val registrationData: StateFlow<ResultType<RegistrationResponse>> = _registrationData

    fun fetchRegistrationData(msisdn:String){
        viewModelScope.launch {
            try {
                val result = repository.getRegistrationData(msisdn)
                _registrationData.value = result
            }catch (e: Exception){
                _registrationData.value = ResultType.Error(e)
            }
        }
    }
}