package com.example.rtv_plus_android_app_revamp.data.models.seeAll

data class SeeAllResponse(
    val catcode: String,
    val catname: String,
    val contents: List<Content>,
    val pagelimit: Int
)