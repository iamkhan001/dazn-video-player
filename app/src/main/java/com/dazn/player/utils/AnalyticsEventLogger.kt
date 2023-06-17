package com.dazn.player.utils

import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class AnalyticsEventLogger {

    private val firebaseAnalytics = Firebase.analytics

    fun logVideoSelectEvent(name: String) {
        val parameters = Bundle().apply {
            this.putString(PARA_SELECT_VIDEO, name)
        }
        firebaseAnalytics.logEvent(EVENT_SELECT_VIDEO, parameters)
    }


    fun logVideoAction(action: String) {
        val parameters = Bundle().apply {
            this.putString(PARA_ACTION, action)
        }
        firebaseAnalytics.logEvent(EVENT_VIDEO_ACTION, parameters)
    }


    companion object {

        private const val EVENT_SELECT_VIDEO = "select_video"
        private const val EVENT_VIDEO_ACTION = "video_action"
        private const val PARA_ACTION = "action"
        private const val PARA_SELECT_VIDEO = "video_name"

        const val ACTION_PLAY_VIDEO = "play_video"
        const val ACTION_PAUSE_VIDEO = "pause_video"
        const val ACTION_RESUME_VIDEO = "resume_video"
        const val ACTION_PLAY_NEXT = "play_next"
        const val ACTION_PLAY_PREVIOUS = "play_previous"

        const val ACTION_SEEK_REPLAY = "seek_replay"
        const val ACTION_SEEK_FORWARD = "seek_forward"

        const val ACTION_SEEK_VIDEO = "seek_video"
        const val ACTION_CLOSE_VIDEO = "close_video"

    }

}