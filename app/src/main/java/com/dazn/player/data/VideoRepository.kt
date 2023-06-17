package com.dazn.player.data

import android.content.Context
import com.dazn.player.data.model.Video
import com.dazn.player.utils.DataConvertor

class VideoRepository(private val context: Context) {

    fun loadVideos(): List<Video> {
        val json = context.assets.open("videos.json").bufferedReader().use { it.readText() }
        return DataConvertor.toVideList(json)
    }

    fun loadTestVideos(): ArrayList<Video> {
        val list = ArrayList<Video>()
        list.add(
            Video(
                "test name",
                "test url"
        ))

        return list
    }

}
