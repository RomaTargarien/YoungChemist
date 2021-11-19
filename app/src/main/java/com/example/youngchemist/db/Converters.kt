package com.example.youngchemist.db

import androidx.room.TypeConverter
import com.example.youngchemist.model.Page
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class Converters {

    @TypeConverter
    fun fromPages(pages: List<Page>): String {
        val gson = Gson()
        val type: Type = object : TypeToken<List<Page?>?>() {}.type
        val json = gson.toJson(pages, type)
        return json
    }

    @TypeConverter
    fun toPages(value: String): List<Page> {
        val gson = Gson()
        val type = object : TypeToken<List<Page?>?>() {}.type
        val pages: List<Page> = gson.fromJson(value, type)
        return pages
    }
}