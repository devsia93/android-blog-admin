package com.devsia.blog.preference

import android.content.Context
import android.content.SharedPreferences
import com.devsia.blog.models.Tag
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferenceHelper(private var context: Context) {

    fun saveListTags(tagList: List<Tag>) =
        saveArrayList(getConvertedTagListToObjectList(tagList), Const.savedTags())

    fun getListTags(): List<Tag> =
        getConvertedObjectArrayListToTagList(getArrayList(Const.savedTags()))


    private fun saveArrayList(list: java.util.ArrayList<Any?>, key: String?) {
        val prefs: SharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        val gson = Gson()
        val json: String = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    private fun getArrayList(key: String?): java.util.ArrayList<Any?> {
        val prefs: SharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val gson = Gson()
        val json: String? = prefs.getString(key, null)
        val listType = object : TypeToken<java.util.ArrayList<Tag?>?>() {}.type

        return gson.fromJson(json, listType)
    }

    private fun getConvertedTagListToObjectList(tagList: List<Tag>): ArrayList<Any?> {
        val result = ArrayList<Any?>()
        for (t in tagList) {
            result.add(t as Any)
        }

        return result
    }

    private fun getConvertedObjectArrayListToTagList(objectList: ArrayList<Any?>): List<Tag> {
        val result: ArrayList<Tag> = ArrayList()

        for (o in objectList)
            result.add(o as Tag)

        return result.toList()
    }
}