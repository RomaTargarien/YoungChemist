package com.example.youngchemist.db

import androidx.room.TypeConverter
import com.example.youngchemist.model.AnswerUser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class Converters {

    @TypeConverter
    fun fromPages(pages: ArrayList<String>): String {
        val gson = Gson()
        val type: Type = object : TypeToken<ArrayList<String?>?>() {}.type
        val json = gson.toJson(pages, type)
        return json
    }

    @TypeConverter
    fun toPages(value: String): ArrayList<String> {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<String?>?>() {}.type
        val pages: ArrayList<String> = gson.fromJson(value, type)
        return pages
    }

    @TypeConverter
    fun fromAnswers(answers: ArrayList<AnswerUser>): String {
        val gson = Gson()
        val type: Type = object : TypeToken<ArrayList<AnswerUser>>() {}.type
        val json = gson.toJson(answers, type)
        return json
    }

    @TypeConverter
    fun toAnswers(value: String): ArrayList<AnswerUser> {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<AnswerUser>?>() {}.type
        val answers: ArrayList<AnswerUser> = gson.fromJson(value, type)
        return answers
    }
}