package com.example.rtv_plus_android_app_revamp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtv_plus_android_app_revamp.data.models.forgetPassword.ForgetPasswordResponse
import com.example.rtv_plus_android_app_revamp.data.repository.ForgetPasswordRepository
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(private val repository: ForgetPasswordRepository) : ViewModel() {
    private val _forgetPasswordData = MutableStateFlow<ResultType<ForgetPasswordResponse>>(ResultType.Loading)
    val forgetPasswordData: StateFlow<ResultType<ForgetPasswordResponse>> = _forgetPasswordData

    fun fetchForgetPasswordData(username: String, password: String, newPass: String){
        viewModelScope.launch {
            try {
                val result = repository.getForgetPasswordData(username, password, newPass)
                _forgetPasswordData.value = result
            }catch (e: Exception){
                _forgetPasswordData.value = ResultType.Error(e)
            }
        }
    }
}