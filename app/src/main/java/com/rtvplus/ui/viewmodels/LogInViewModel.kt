package com.rtvplus.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtvplus.data.models.logIn.LogInResponse
import com.rtvplus.data.repository.LogInRepository
import com.rtvplus.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(private val repository: LogInRepository) : ViewModel() {
    private val _logInData = MutableStateFlow<ResultType<LogInResponse>>(ResultType.Loading)
    val logInData: StateFlow<ResultType<LogInResponse>> = _logInData
    fun fetchLogInData(userName: String, password: String, haspin: String, tc:String,) {
        viewModelScope.launch {
            try {
                val result = repository.getLogInData(userName, password, haspin, tc)
                _logInData.value = result
            } catch (e: Exception) {
                _logInData.value = ResultType.Error(e)
            }
        }
    }
}