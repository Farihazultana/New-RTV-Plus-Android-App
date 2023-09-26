package com.example.rtv_plus_android_app_revamp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtv_plus_android_app_revamp.data.models.logIn.LogInResponse
import com.example.rtv_plus_android_app_revamp.data.repository.LogInRepository
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(private val repository: LogInRepository) : ViewModel() {
    private val _logInData = MutableStateFlow<ResultType<LogInResponse>>(ResultType.Loading)
    val logInData: StateFlow<ResultType<LogInResponse>> = _logInData
    fun fetchLogInData(userName: String, password: String, haspin: String) {
        viewModelScope.launch {
            try {
                val result = repository.getLogInData(userName, password, haspin)
                _logInData.value = result
            } catch (e: Exception) {
                _logInData.value = ResultType.Error(e)
            }
        }
    }
}