package com.rtvplus.utils

import android.content.Context
import android.content.SharedPreferences

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


    fun getData(context: Context, key: String, defaultValue: Any): Any {
        val sharedPreferences = getSharedPreferences(context)
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue) ?: defaultValue
            is Int -> sharedPreferences.getInt(key, defaultValue)
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue)
            is Float -> sharedPreferences.getFloat(key, defaultValue)
            is Long -> sharedPreferences.getLong(key, defaultValue)
            else -> throw IllegalArgumentException("Unsupported data type")
        }
    }

    fun removeKey(context: Context, key: String) {
        getSharedPreferences(context).edit().remove(key).apply()
    }

    fun clear(context: Context) {
        getSharedPreferences(context).edit().clear().apply()
    }
}