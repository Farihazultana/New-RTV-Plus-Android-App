package com.rtvplus.data.models.home

data class Data(
    val catcode: String,
    val catname: String,
    val contents: List<Content>,
    val contenttype: String,
    val contentviewtype: String,
    val dt: String,
    val maincategory: String,
    val resolution: String,
    val tc: String
)