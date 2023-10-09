package com.rtvplus.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtvplus.data.models.favorite_list.AddListResponse
import com.rtvplus.data.repository.AddFavoriteListRepository
import com.rtvplus.utils.ResultType
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