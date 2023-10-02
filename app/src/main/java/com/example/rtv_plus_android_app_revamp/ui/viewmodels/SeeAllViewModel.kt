package com.example.rtv_plus_android_app_revamp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtv_plus_android_app_revamp.data.models.seeAll.SeeAllResponse
import com.example.rtv_plus_android_app_revamp.data.repository.SeeAllRepository
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SeeAllViewModel @Inject constructor(private val repository: SeeAllRepository) : ViewModel() {
    private val _seeAllData = MutableStateFlow<ResultType<SeeAllResponse>>(ResultType.Loading)
    val seeAllData : StateFlow<ResultType<SeeAllResponse>> = _seeAllData
    fun fetchSeeAllData(page: String, ct: String, tc: String, testval: String){
        viewModelScope.launch {
            try {
                val result = repository.getSeeAllData(page,ct,tc,testval)
                _seeAllData.value=result
            } catch (e: Exception) {
                _seeAllData.value = ResultType.Error(e)
            }
        }
    }

}