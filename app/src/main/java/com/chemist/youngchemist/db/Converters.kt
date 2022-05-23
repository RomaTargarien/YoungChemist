package com.chemist.youngchemist.db

import androidx.room.TypeConverter
import com.chemist.youngchemist.model.Test
import com.chemist.youngchemist.model.user.AnswerUser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class Converters {

    @TypeConverter
    fun fromTest(test: Test?): String {
        val gson = Gson()
        val type: Type = object : TypeToken<Test?>() {}.type
        return gson.toJson(test, type)
    }

    @TypeConverter
    fun toTest(value: String): Test? {
        val gson = Gson()
        val type = object : TypeToken<Test?>() {}.type
        return gson.fromJson(value, type)
    }


    @TypeConverter
    fun fromAnswers(answers: ArrayList<AnswerUser>): String {
        val gson = Gson()
        val type: Type = object : TypeToken<ArrayList<AnswerUser>>() {}.type
        return gson.toJson(answers, type)
    }

    @TypeConverter
    fun toAnswers(value: String): ArrayList<AnswerUser> {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<AnswerUser>?>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromPages(pages: ArrayList<String>): String {
        val gson = Gson()
        val type: Type = object : TypeToken<ArrayList<String?>?>() {}.type
        return gson.toJson(pages, type)
    }

    @TypeConverter
    fun toPages(value: String): ArrayList<String> {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<String?>?>() {}.type
        return gson.fromJson(value, type)
    }

}