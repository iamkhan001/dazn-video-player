package com.dazn.player.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dazn.player.data.model.Video
import com.dazn.player.ui.screens.main.MainViewModel

@Composable
fun HomeScreen(title: String, navigateToVideo: (Int, Video) -> Unit) {
    val mainViewModel = hiltViewModel<MainViewModel>()
    val videos = remember { mainViewModel.videoList }

    videos.value?.let {

        val configuration = LocalConfiguration.current
        when (configuration.orientation) {

            //show grid if layout is landscape
            Configuration.ORIENTATION_LANDSCAPE -> {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .wrapContentHeight()
                            .padding(vertical = 25.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            title,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 256.dp)
                    ) {

                        itemsIndexed(it) { index, video ->
                            VideoListItemVertical(index=index, video = video, navigateToVideo = navigateToVideo )
                        }
                    }
                }
            }
            else -> {

                //show vertical list of videos for pertrait screens
                LazyColumn(
                    Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .wrapContentHeight()
                                .padding(vertical = 25.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                title,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    itemsIndexed(it) { index, video ->
                        VideoListItem(index=index, video = video, navigateToVideo = navigateToVideo )
                    }
                }
            }
        }

    }

}
