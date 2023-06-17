package com.dazn.player.utils

import com.dazn.player.data.model.Video
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataConvertor {

    fun toVideList(json: String): List<Video> {
        val typeToken = object : TypeToken<List<Video>>() {}.type
        return Gson().fromJson(json, typeToken)
    }

}