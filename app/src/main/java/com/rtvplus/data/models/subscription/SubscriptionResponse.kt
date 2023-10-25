package com.rtvplus.data.models.subscription

data class SubscriptionResponse(
	val subschemes: ArrayList<SubschemesItem>
)

data class SubschemesItem(
	val id: String,
	val iappackcode: String,
	val userpack: String,
	val packtext: String,
	val pack_name: String,
	val sub_pack: String,
	val sub_text: String,
	val iapsub_text: String,
	val reg_msg: String,
	val msisdn: String
)

