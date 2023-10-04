package com.example.rtv_plus_android_app_revamp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtv_plus_android_app_revamp.data.models.favorite_list.AddListResponse
import com.example.rtv_plus_android_app_revamp.data.models.favorite_list.RemoveListResponse
import com.example.rtv_plus_android_app_revamp.data.repository.AddFavoriteListRepository
import com.example.rtv_plus_android_app_revamp.data.repository.RemoveFavoriteListRepository
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemoveFavoriteListViewModel @Inject constructor(private val removeFavoriteListRepository: RemoveFavoriteListRepository) :
    ViewModel() {
    private val _removeContentResponse = MutableLiveData<ResultType<RemoveListResponse>>(ResultType.Loading)
    val removeContentResponse: LiveData<ResultType<RemoveListResponse>> = _removeContentResponse
    fun removeFavoriteContent(myval: String, username: String) {
        viewModelScope.launch {
            try {
                val result = removeFavoriteListRepository.removeFavoriteContents(myval, username)
                _removeContentResponse.value = result
            } catch (e: Exception) {
                _removeContentResponse.value = ResultType.Error(e)
            }
        }
    }
}