package com.dazn.player.ui.screens.player

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.dazn.player.ui.theme.DAZNPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class VideoPlayerActivity : ComponentActivity() {

    companion object {

        private const val ARG_INDEX = "index"
        fun start(context: Context, index: Int) {
            val intent = Intent(context, VideoPlayerActivity::class.java)
            intent.putExtra(ARG_INDEX, index)
            context.startActivity(intent)
        }

    }

    private val videoViewModel: VideoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DAZNPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VideoPlayerView()
                }
            }
        }

        val index = intent.getIntExtra(ARG_INDEX, 0)

        videoViewModel.initVideo(index)
    }

}

