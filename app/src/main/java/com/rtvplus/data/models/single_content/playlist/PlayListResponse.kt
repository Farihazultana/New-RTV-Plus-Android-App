package com.rtvplus.data.models.single_content.playlist

data class PlayListResponse(
    val dramacover: String,
    val dramaname: String,
    val episodelist: List<Episodelist>,
    val sharable: String
)