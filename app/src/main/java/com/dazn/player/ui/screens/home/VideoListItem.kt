package com.dazn.player.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.dazn.player.R
import com.dazn.player.data.model.Video


@Composable
fun VideoListItem(index: Int, video: Video, navigateToVideo: (Int, Video) -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
    ) {
        Row(Modifier.clickable { navigateToVideo(index, video) }) {
            VideoThumbImage(video)
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = video.name, style = typography.bodyMedium)
            }
        }
    }
}

@Composable
fun VideoListItemVertical(index: Int, video: Video, navigateToVideo: (Int, Video) -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
    ) {
        Column(
            Modifier
                .clickable { navigateToVideo(index, video) }
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            VideoThumbImage(video)
            Text(
                text = video.name,
                style = typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

//wanted to load thumb from video url but file format is .mdp which is not load using glide
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun VideoThumbImage(video: Video) {
    Image(
        painter = painterResource(id = R.drawable.ic_play_circle),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .padding(8.dp)
            .size(84.dp)
            .clip(RoundedCornerShape(corner = CornerSize(16.dp)))
    )
//    GlideImage(
//        model = video.uri,
//        contentScale = ContentScale.Crop,
//        contentDescription = video.name,
//        modifier = Modifier
//            .padding(8.dp)
//            .size(84.dp)
//            .clip(RoundedCornerShape(corner = CornerSize(16.dp)))
//
//    ) { it.optionalFitCenter() }

}

