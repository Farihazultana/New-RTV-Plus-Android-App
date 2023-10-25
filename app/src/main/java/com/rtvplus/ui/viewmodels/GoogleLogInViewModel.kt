package com.rtvplus.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtvplus.data.models.socialmedia_login.google.GoogleLogInResponse
import com.rtvplus.data.repository.GoogleLogInRepository
import com.rtvplus.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class GoogleLogInViewModel @Inject constructor(private val repository: GoogleLogInRepository):ViewModel(){
    private val _googleLogInData = MutableLiveData<ResultType<GoogleLogInResponse>>(ResultType.Loading)
    val googleLogInData : MutableLiveData<ResultType<GoogleLogInResponse>> = _googleLogInData

    fun fetchGoogleLogInData(logintype: String, source: String, username: String, password: String, firstName: String, lastName: String, email: String, imgUrl: String){
        viewModelScope.launch {
            try {
                val result = repository.getGoogleLogInData(logintype,source,username,password,firstName,lastName,email,imgUrl)
                _googleLogInData.value = result
            }catch (e: Exception){
                _googleLogInData.value = ResultType.Error(e)
            }
        }

    }
}