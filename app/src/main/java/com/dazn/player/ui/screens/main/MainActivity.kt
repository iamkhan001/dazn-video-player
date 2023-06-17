package com.dazn.player.ui.screens.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dazn.player.R
import com.dazn.player.ui.screens.home.HomeScreen
import com.dazn.player.ui.screens.player.VideoPlayerActivity
import com.dazn.player.ui.theme.DAZNPlayerTheme
import com.dazn.player.utils.NetworkUtils
import com.dazn.player.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { mainViewModel.isLoading.value }
        }
        setContent {
            DAZNPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen (getString(R.string.app_name)){  index, video ->
                        if (mainViewModel.isInternetAvailable.value) {
                            //using separate activity to show player screen, I think it's better way to make video player in another activity
                            VideoPlayerActivity.start(this, index)
                        }else {
                            showToast(getString(R.string.no_internet_connection))
                        }
                    }
                }
            }
        }

        NetworkUtils.getNetworkLiveData(this).observe(this) {
            Log.d(TAG,"NetworkUtils: Internet Available > $it")
            mainViewModel.isInternetAvailable.value = it
        }

    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DAZNPlayerTheme {
        HomeScreen("Android") {
                index, video ->
        }
    }
}