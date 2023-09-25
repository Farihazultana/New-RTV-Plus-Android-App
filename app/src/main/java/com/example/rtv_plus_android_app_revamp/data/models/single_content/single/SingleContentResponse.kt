package com.example.rtv_plus_android_app_revamp.data.models.single_content.single

data class SingleContentResponse(
    val castinginfo: String,
    val castinginfos: List<Castinginfo>,
    val catcode: String,
    val cp: String,
    val episode: String,
    val episodelist: String,
    val freecontent: Int,
    val genre: String,
    val hd: String,
    val id: String,
    val image_location: String,
    val info: String,
    val length: String,
    val length2: String,
    val mylist: Int,
    val name: String,
    val otherpeople: String,
    val otherpeoples: List<Otherpeople>,
    val performer: String,
    val playposition: Int,
    val publishedon: String,
    val rating: String,
    val released: String,
    val serialname: Any,
    val sharable: String,
    val showad: Int,
    val similar: List<Similar>,
    val type: String,
    val url: String,
    val url_hd: String,
    val urlpreview: String
)