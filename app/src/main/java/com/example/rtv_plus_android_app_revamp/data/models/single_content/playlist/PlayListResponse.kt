package com.example.rtv_plus_android_app_revamp.data.models.single_content.playlist

data class PlayListResponse(
    val dramacover: String,
    val dramaname: String,
    val episodelist: List<Episodelist>,
    val sharable: String
)