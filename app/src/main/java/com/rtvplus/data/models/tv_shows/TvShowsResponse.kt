package com.rtvplus.data.models.tv_shows

data class TvShowsResponse(
    val episodelist: List<Episodelist>,
    val name: String
)