package com.example.rtv_plus_android_app_revamp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtv_plus_android_app_revamp.data.models.seeAll.SeeAllResponse
import com.example.rtv_plus_android_app_revamp.data.models.signIn.SignInResponse
import com.example.rtv_plus_android_app_revamp.data.repository.SignInRepository
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val repository: SignInRepository):ViewModel(){
    private val _signInData = MutableStateFlow<ResultType<SignInResponse>>(ResultType.Loading)
    val seeAllData : StateFlow<ResultType<SignInResponse>> = _signInData
    fun fetchSeeAllData(userName: String, password: String, haspin: String){
        viewModelScope.launch {
            try {
                val result = repository.getSignInData(userName,password,haspin)
                _signInData.value=result
            } catch (e: Exception) {
                _signInData.value = ResultType.Error(e)
            }
        }
    }
}