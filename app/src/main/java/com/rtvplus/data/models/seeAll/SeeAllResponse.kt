package com.rtvplus.data.models.seeAll

data class SeeAllResponse(
    val catcode: String,
    val catname: String,
    val contents: List<Content>,
    val pagelimit: Int
)