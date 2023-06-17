package com.dazn.player.ui.screens.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dazn.player.data.VideoRepository
import com.dazn.player.data.model.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val videoRepository: VideoRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)

    val isLoading get() = _isLoading.asStateFlow()

    val videoList: MutableState<List<Video>?> = mutableStateOf(null)

    init {
        viewModelScope.launch {
            //load video list from json file in assets
            videoList.value = videoRepository.loadVideos()
            //delay to show loading animation
            delay(1000)
            _isLoading.value = false
        }
    }
}