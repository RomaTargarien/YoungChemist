package com.example.youngchemist.db

import androidx.room.TypeConverter
import com.example.youngchemist.model.AnswerUser
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

    @TypeConverter
    fun fromAnswers(answers: ArrayList<AnswerUser>): String {
        val gson = Gson()
        val type: Type = object : TypeToken<ArrayList<AnswerUser>>() {}.type
        val json = gson.toJson(answers,type)
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