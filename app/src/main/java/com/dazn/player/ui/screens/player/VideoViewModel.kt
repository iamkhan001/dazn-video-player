package com.dazn.player.ui.screens.player

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dazn.player.data.VideoRepository
import com.dazn.player.data.model.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(private val videoRepository: VideoRepository) : ViewModel() {

    private val videoList: MutableState<List<Video>?> = mutableStateOf(null)

    var videoIndex = 0
    var currentVideoToPlay: Video? =null

    init {
        viewModelScope.launch {
            videoList.value = videoRepository.loadVideos()
        }
    }

    fun initVideo(index: Int) = viewModelScope.launch {
        val videos = videoList.value
        if (videos.isNullOrEmpty()) {
            return@launch
        }
        videoIndex = index
        Log.e("VMV", "index = $videoIndex")

        if (videoIndex in videos.indices) {
            currentVideoToPlay = videos[videoIndex]
            return@launch
        }
    }

    fun playNextVideo(): Boolean {
        val videos = videoList.value

        Log.e(TAG,"playNextVideo: $videos, $videoIndex")

        if (videos.isNullOrEmpty()) {
            return false
        }

        if(videoIndex < videos.size-1) {
            videoIndex+=1

            if (videoIndex in videos.indices) {
                currentVideoToPlay = videos[videoIndex]
                return true
            }
        }

        return false
    }

    fun playPreviousVideo(): Boolean {
        val videos = videoList.value

        Log.e(TAG,"playPreviousVideo: $videos, $videoIndex")

        if (videos.isNullOrEmpty()) {
            return false
        }

        if (videoIndex > 0) {
            videoIndex-=1

            if (videoIndex in videos.indices) {
                currentVideoToPlay = videos[videoIndex]
                return true
            }

        }

        return false
    }

    fun isNextVideoAvailable(): Boolean = videoIndex < ((videoList.value?.size ?: 0) - 1)

    fun isPreviousVideoAvailable(): Boolean = videoIndex > 0

    companion object {
        private const val TAG = "VideoViewModel"
    }

}