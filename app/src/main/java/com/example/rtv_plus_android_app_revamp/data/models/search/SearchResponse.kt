package com.example.rtv_plus_android_app_revamp.data.models.search

data class SearchResponse(
    val catname: String,
    val contents: List<Content>,
    val pagelimit: Int
)