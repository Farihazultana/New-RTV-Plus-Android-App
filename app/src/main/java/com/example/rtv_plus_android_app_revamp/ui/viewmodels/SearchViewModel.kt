package com.example.rtv_plus_android_app_revamp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rtv_plus_android_app_revamp.data.models.search.SearchResponse
import com.example.rtv_plus_android_app_revamp.data.repository.SearchRepository
import com.example.rtv_plus_android_app_revamp.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchRepository: SearchRepository) : ViewModel() {
    private val _searchData = MutableLiveData<ResultType<SearchResponse>>(ResultType.Loading)
    val searchData: MutableLiveData<ResultType<SearchResponse>> = _searchData
    fun fetchSearchData(fromsrc: String, s: String) {
        viewModelScope.launch {
            try {
                val result = searchRepository.getSearchResultData(fromsrc, s)
                _searchData.value = result
            } catch (e: Exception) {
                _searchData.value = ResultType.Error(e)
                Log.e("HomeViewModel", e.toString())
            }
        }
    }
}