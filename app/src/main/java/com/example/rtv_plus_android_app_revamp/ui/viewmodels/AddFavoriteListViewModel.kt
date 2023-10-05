package com.example.rtv_plus_android_app_revamp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtv_plus_android_app_revamp.data.models.favorite_list.AddListResponse
import com.example.rtv_plus_android_app_revamp.data.models.favorite_list.FavoriteResponse
import com.example.rtv_plus_android_app_revamp.data.repository.AddFavoriteListRepository
import com.example.rtv_plus_android_app_revamp.data.repository.FavoriteListRepository
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFavoriteListViewModel @Inject constructor(private val addFavoriteListRepository: AddFavoriteListRepository) :
    ViewModel() {
    private val _addContentResponse = MutableLiveData<ResultType<AddListResponse>>(ResultType.Loading)
    val addContentResponse: LiveData<ResultType<AddListResponse>> = _addContentResponse
    fun addFavoriteContent(myval: String, username: String) {
        viewModelScope.launch {
            try {
                val result = addFavoriteListRepository.addFavoriteContents(myval, username)
                _addContentResponse.value = result
            } catch (e: Exception) {
                _addContentResponse.value = ResultType.Error(e)
            }
        }
    }
}