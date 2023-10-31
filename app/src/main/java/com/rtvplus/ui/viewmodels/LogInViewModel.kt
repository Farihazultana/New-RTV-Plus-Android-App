package com.rtvplus.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtvplus.data.models.logIn.LogInResponse
import com.rtvplus.data.repository.LogInRepository
import com.rtvplus.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(private val repository: LogInRepository) : ViewModel() {
    private var _logInData = MutableLiveData<ResultType<LogInResponse>>(ResultType.Loading)
    val logInData: LiveData<ResultType<LogInResponse>> = _logInData

    fun fetchLogInData(userName: String, password: String, haspin: String, tc:String) {
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