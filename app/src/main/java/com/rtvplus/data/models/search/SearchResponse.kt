package com.rtvplus.data.models.search

data class SearchResponse(
    val catname: String,
    val contents: List<Content>,
    val pagelimit: Int
)