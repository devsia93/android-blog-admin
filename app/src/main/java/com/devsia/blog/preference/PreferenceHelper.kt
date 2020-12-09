package com.devsia.blog.preference

import android.content.Context
import android.content.SharedPreferences
import com.devsia.blog.models.Tag
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferenceHelper {
    companion object Helper {

        fun saveListTags(context: Context, tagList: List<Tag>) =
            saveArrayList(context, getConvertedTagListToObjectList(tagList), Const.savedTags())

        fun getListTags(context: Context): List<Tag> =
            getConvertedObjectArrayListToTagList(getArrayList(context, Const.savedTags()))

        fun getToken(context: Context): String? {
            val prefs: SharedPreferences =
                context.getSharedPreferences(Const.savedToken(), Context.MODE_PRIVATE)

            return prefs.getString("token", null)
        }

        fun setToken(context: Context, token: String) {
            val prefs: SharedPreferences =
                context.getSharedPreferences(Const.savedToken(), Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = prefs.edit()
            if (token.length > 6) {
                if (token.substring(1, 6) != "Token")
                    editor.putString("token", "Token $token")
                else
                    editor.putString("token", token)

                editor.apply()
            }
        }

        private fun saveArrayList(context: Context, list: java.util.ArrayList<Any?>, key: String?) {
            val prefs: SharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = prefs.edit()
            val gson = Gson()
            val json: String = gson.toJson(list)
            editor.putString(key, json)
            editor.apply()
        }

        private fun getArrayList(context: Context, key: String?): java.util.ArrayList<Any?> {
            val prefs: SharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            val gson = Gson()
            val json: String? = prefs.getString(key, null)
            val listType = object : TypeToken<java.util.ArrayList<Tag?>?>() {}.type

            return gson.fromJson(json, listType)
        }

        private fun getConvertedTagListToObjectList(tagList: List<Tag>): ArrayList<Any?> {
            val result = ArrayList<Any?>()
            for (tag in tagList) {
                result.add(tag as Any)
            }

            return result
        }

        private fun getConvertedObjectArrayListToTagList(objectList: ArrayList<Any?>): List<Tag> {
            val result: ArrayList<Tag> = ArrayList()

            for (obj in objectList)
                result.add(obj as Tag)

            return result.toList()
        }
    }
}