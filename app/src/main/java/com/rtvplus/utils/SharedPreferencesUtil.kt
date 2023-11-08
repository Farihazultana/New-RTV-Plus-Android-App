package com.rtvplus.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rtvplus.data.models.logIn.LogInResponseItem
import com.rtvplus.data.models.logIn.SocialLoginData
import java.lang.reflect.Type

object SharedPreferencesUtil {
    private const val PREF_NAME = "SP_Authentication"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveData(context: Context, key: String, value: Any) {
        val editor = getSharedPreferences(context).edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
            else -> throw IllegalArgumentException("Unsupported data type")
        }
        editor.apply()
    }

    fun getData(context: Context, key: String, defaultValue: Any?): Any {
        val sharedPreferences = getSharedPreferences(context)
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue.toString()) ?: defaultValue
            is Int -> sharedPreferences.getInt(key, defaultValue.toInt())
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue)
            is Float -> sharedPreferences.getFloat(key, defaultValue.toFloat())
            is Long -> sharedPreferences.getLong(key, defaultValue.toLong())
            else -> throw IllegalArgumentException("Unsupported data type")
        }
    }

    fun saveLogInData(context: Context, list: LogInResponseItem?) {
        val editor = getSharedPreferences(context).edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(AppUtils.LogInObj, json)
        editor.apply()
    }

    fun getSavedLogInData(context: Context): LogInResponseItem? {
        val gson = Gson()
        val json: String? = getSharedPreferences(context).getString(AppUtils.LogInObj, null)
        val type: Type = object : TypeToken<LogInResponseItem?>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveSocialLogInData(context: Context, list: SocialLoginData?){
        val editor = getSharedPreferences(context).edit()
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(AppUtils.SocialLogInObj, json)
        editor.apply()
    }

    fun getSavedSocialLogInData(context: Context): SocialLoginData? {
        val gson = Gson()
        val json: String? = getSharedPreferences(context).getString(AppUtils.SocialLogInObj, null)
        val type: Type = object : TypeToken<SocialLoginData?>() {}.type
        return gson.fromJson(json, type)
    }

    fun removeKey(context: Context, key: String) {
        getSharedPreferences(context).edit().remove(key).apply()
    }

    fun clear(context: Context) {
        getSharedPreferences(context).edit().clear().apply()
    }
}