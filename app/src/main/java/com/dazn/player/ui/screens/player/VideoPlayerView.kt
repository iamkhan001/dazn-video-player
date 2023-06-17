package com.dazn.player.ui.screens.player

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.dazn.player.R
import com.dazn.player.data.model.Video
import com.dazn.player.ui.theme.Purple200
import java.util.concurrent.TimeUnit

private const val TAG = "PlayerView"

@Composable
@UnstableApi
@androidx.annotation.OptIn(UnstableApi::class)
fun VideoPlayerView(modifier: Modifier = Modifier) {

    val videoViewModel = hiltViewModel<VideoViewModel>()
    val context = LocalContext.current
    val video = videoViewModel.currentVideoToPlay

    if (video == null) {
        Text(
            text = "Failed to load video!",
        )
        return
    }
    val exoPlayer = remember {
        intiExoplayer(context, video)
    }

    exoPlayer.playWhenReady = true
    exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
    exoPlayer.repeatMode = Player.REPEAT_MODE_ONE


    val shouldShowControls = remember { mutableStateOf(false) }
    val isPlaying = remember { mutableStateOf(exoPlayer.isPlaying) }
    val totalDuration = remember { mutableStateOf(0L) }
    val currentTime = remember { mutableStateOf(0L) }
    val bufferedPercentage = remember { mutableStateOf(0) }
    val playbackState = remember { mutableStateOf(exoPlayer.playbackState) }

    Box(modifier = modifier) {
        DisposableEffect(key1 = Unit) {
            val listener =
                object : Player.Listener {
                    override fun onEvents(
                        player: Player,
                        events: Player.Events
                    ) {
                        super.onEvents(player, events)
                        totalDuration.value = player.duration.coerceAtLeast(0L)
                        currentTime.value = player.currentPosition.coerceAtLeast(0L)
                        bufferedPercentage.value = player.bufferedPercentage
                        isPlaying.value = player.isPlaying
                        playbackState.value = player.playbackState
                    }
                }

            exoPlayer.addListener(listener)

            onDispose {
                exoPlayer.removeListener(listener)
                exoPlayer.release()
            }
        }

        AndroidView(
            modifier =
            Modifier.clickable {
                shouldShowControls.value = shouldShowControls.value.not()
            },
            factory = {
                PlayerView(context).apply {
                    hideController()
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

                    player = exoPlayer
                    layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
            }
        )
        PlayerControls(
            modifier = Modifier.fillMaxSize(),
            isVisible = { shouldShowControls.value },
            isPlaying = { isPlaying.value },
            title = { exoPlayer.mediaMetadata.displayTitle.toString() },
            onPlayNext = {
                shouldShowControls.value = false
                if(videoViewModel.playNextVideo()) {
                    Log.d(TAG,"onPlayNext: $video")
                    playAnotherVideo(exoPlayer, videoViewModel.currentVideoToPlay)
                }else {
                    Log.e(TAG,"No next video to play")
                }
            },
            onPlayPrevious = {
                shouldShowControls.value = false
                if(videoViewModel.playPreviousVideo()) {
                    Log.d(TAG,"onPlayPrevious: $video")
                    playAnotherVideo(exoPlayer, videoViewModel.currentVideoToPlay)
                }else {
                    Log.e(TAG,"No previous video to play")
                }
            },
            playbackState = { playbackState.value },
            onReplayClick = { exoPlayer.seekBack() },
            onForwardClick = { exoPlayer.seekForward() },
            onPauseToggle = {
                when {
                    exoPlayer.isPlaying -> {
                        // pause the video
                        exoPlayer.pause()
                    }
                    exoPlayer.isPlaying.not() &&
                            playbackState.value == STATE_ENDED -> {
                        exoPlayer.seekTo(0)
                        exoPlayer.playWhenReady = true
                    }
                    else -> {
                        // play the video
                        // it's already paused
                        exoPlayer.play()
                    }
                }
                isPlaying.value = isPlaying.value.not()
            },
            totalDuration = { totalDuration.value },
            currentTime = { currentTime.value },
            bufferedPercentage = { bufferedPercentage.value },
            onSeekChanged = { timeMs: Float ->
                exoPlayer.seekTo(timeMs.toLong())
            },
            showNextButton = videoViewModel.isNextVideoAvailable(),
            showPreviousButton = videoViewModel.isPreviousVideoAvailable(),
        )
    }

}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun intiExoplayer(context: Context, video: Video): ExoPlayer {
    val player = ExoPlayer.Builder(context)
        .apply {
            setSeekBackIncrementMs(PLAYER_SEEK_BACK_INCREMENT)
            setSeekForwardIncrementMs(PLAYER_SEEK_FORWARD_INCREMENT)
        }
        .build()
        .apply {
            val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()

            val source = DashMediaSource.Factory(defaultHttpDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(video.uri))

            setMediaSource(source)
            prepare()
        }

    return player
}
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun playAnotherVideo(exoPlayer: ExoPlayer, video: Video?) {

    if (video == null) {
        return
    }

    val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()

    val source = DashMediaSource.Factory(defaultHttpDataSourceFactory)
        .createMediaSource(MediaItem.fromUri(video.uri))

    exoPlayer.setMediaSource(source)
    exoPlayer.prepare()
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PlayerControls(
    modifier: Modifier = Modifier,
    isVisible: () -> Boolean,
    isPlaying: () -> Boolean,
    title: () -> String,
    onPlayPrevious: () -> Unit,
    onPlayNext: () -> Unit,
    onReplayClick: () -> Unit,
    onForwardClick: () -> Unit,
    onPauseToggle: () -> Unit,
    totalDuration: () -> Long,
    currentTime: () -> Long,
    bufferedPercentage: () -> Int,
    playbackState: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit,
    showNextButton: Boolean,
    showPreviousButton: Boolean
) {

    val visible = remember(isVisible()) { isVisible() }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = Modifier.background(Color.Black.copy(alpha = 0.6f))) {
            TopControl(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth(),
                title = title
            )

            CenterControls(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                isPlaying = isPlaying,
                onPlayPrevious = onPlayPrevious,
                onPlayNext = onPlayNext,
                onReplayClick = onReplayClick,
                onForwardClick = onForwardClick,
                onPauseToggle = onPauseToggle,
                playbackState = playbackState,
                showNextButton = showNextButton,
                showPreviousButton = showPreviousButton,
            )

            BottomControls(
                modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .animateEnterExit(
                        enter =
                        slideInVertically(
                            initialOffsetY = { fullHeight: Int ->
                                fullHeight
                            }
                        ),
                        exit =
                        slideOutVertically(
                            targetOffsetY = { fullHeight: Int ->
                                fullHeight
                            }
                        )
                    ),
                totalDuration = totalDuration,
                currentTime = currentTime,
                bufferedPercentage = bufferedPercentage,
                onSeekChanged = onSeekChanged
            )
        }
    }
}

@Composable
private fun TopControl(modifier: Modifier = Modifier, title: () -> String) {
    val videoTitle = remember(title()) { title() }

    Text(
        modifier = modifier.padding(16.dp),
        text = videoTitle,
        style = MaterialTheme.typography.bodyMedium,
        color = Purple200
    )
}

@Composable
private fun CenterControls(
    modifier: Modifier = Modifier,
    isPlaying: () -> Boolean,
    playbackState: () -> Int,
    onPlayPrevious: () -> Unit,
    onPlayNext: () -> Unit,
    onReplayClick: () -> Unit,
    onPauseToggle: () -> Unit,
    onForwardClick: () -> Unit,
    showNextButton: Boolean,
    showPreviousButton: Boolean
) {
    val isVideoPlaying = remember(isPlaying()) { isPlaying() }

    val playerState = remember(playbackState()) { playbackState() }

    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceEvenly) {

        if (showPreviousButton) {
            IconButton(modifier = Modifier.size(40.dp), onClick = onPlayPrevious) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    painter = painterResource(id = R.drawable.ic_skip_previous),
                    contentDescription = "Replay 5 seconds"
                )
            }
        }else {
            IconButton(modifier = Modifier.size(40.dp), onClick = {}) {

            }
        }

        IconButton(modifier = Modifier.size(40.dp), onClick = onReplayClick) {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter = painterResource(id = R.drawable.ic_replay_5),
                contentDescription = "Replay 5 seconds"
            )
        }

        IconButton(modifier = Modifier.size(40.dp), onClick = onPauseToggle) {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter =
                when {
                    isVideoPlaying -> {
                        painterResource(id = R.drawable.ic_pause)
                    }
                    isVideoPlaying.not() && playerState == STATE_ENDED -> {
                        painterResource(id = R.drawable.ic_replay)
                    }
                    else -> {
                        painterResource(id = R.drawable.ic_play)
                    }
                },
                contentDescription = "Play/Pause"
            )
        }

        IconButton(modifier = Modifier.size(40.dp), onClick = onForwardClick) {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter = painterResource(id = R.drawable.ic_forward_10),
                contentDescription = "Forward 10 seconds"
            )
        }

        if (showNextButton) {
            IconButton(modifier = Modifier.size(40.dp), onClick = onPlayNext) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    painter = painterResource(id = R.drawable.ic_skip_next),
                    contentDescription = "Forward 10 seconds"
                )
            }
        }else {
            IconButton(modifier = Modifier.size(40.dp), onClick = {}) {

            }
        }
    }
}

@Composable
private fun BottomControls(
    modifier: Modifier = Modifier,
    totalDuration: () -> Long,
    currentTime: () -> Long,
    bufferedPercentage: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit
) {

    val duration = remember(totalDuration()) { totalDuration() }

    val videoTime = remember(currentTime()) { currentTime() }

    val buffer = remember(bufferedPercentage()) { bufferedPercentage() }

    Column(modifier = modifier.padding(bottom = 32.dp)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = buffer.toFloat(),
                enabled = false,
                onValueChange = { /*do nothing*/},
                valueRange = 0f..100f,
                colors =
                SliderDefaults.colors(
                    disabledThumbColor = Color.Transparent,
                    disabledActiveTrackColor = Color.Gray
                )
            )

            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = videoTime.toFloat(),
                onValueChange = onSeekChanged,
                valueRange = 0f..duration.toFloat(),
                colors =
                SliderDefaults.colors(
                    thumbColor = Purple200,
                    activeTickColor = Purple200
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = duration.formatMinSec(),
                color = Purple200
            )
        }
    }
}

fun Long.formatMinSec(): String {
    return if (this == 0L) {
        "..."
    } else {
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(this),
            TimeUnit.MILLISECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(this)
                    )
        )
    }
}

private const val PLAYER_SEEK_BACK_INCREMENT = 5 * 1000L // 5 seconds
private const val PLAYER_SEEK_FORWARD_INCREMENT = 10 * 1000L // 10 seconds