package com.rtvplus.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rtvplus.data.models.tv_shows.TvShowsResponse
import com.rtvplus.data.repository.TvShowsRepository
import com.rtvplus.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvShowsViewModel @Inject constructor(private val tvShowsRepository: TvShowsRepository) :
    ViewModel() {
    private val _content = MutableLiveData<ResultType<TvShowsResponse>>(ResultType.Loading)
    val content: MutableLiveData<ResultType<TvShowsResponse>> = _content
    fun fetchTvShowsContent(username: String, cc: String, resolution: String) {
        viewModelScope.launch {
            try {
                val result = tvShowsRepository.getTvShowsListData(username, cc, resolution)
                _content.value = result
            } catch (e: Exception) {
                _content.value = ResultType.Error(e)
                Log.e("TvShowsViewModel", e.toString())
            }
        }
    }
}