package com.example.rtv_plus_android_app_revamp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtv_plus_android_app_revamp.data.models.favorite_list.FavoriteResponse
import com.example.rtv_plus_android_app_revamp.data.repository.FavoriteListRepository
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteListViewModel @Inject constructor(private val favoriteListRepository: FavoriteListRepository) :
    ViewModel() {
    private val _favoriteContent = MutableLiveData<ResultType<FavoriteResponse>>(ResultType.Loading)
    val favoriteContent: LiveData<ResultType<FavoriteResponse>> = _favoriteContent
    fun fetchFavoriteContent(username: String, page: String) {
        viewModelScope.launch {
            try {
                val result = favoriteListRepository.getFavoriteContents(username, page)
                _favoriteContent.value = result
            } catch (e: Exception) {
                _favoriteContent.value = ResultType.Error(e)
            }
        }
    }
}