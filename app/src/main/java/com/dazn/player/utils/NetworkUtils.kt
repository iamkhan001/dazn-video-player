package com.dazn.player.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Network Utility to detect availability or unavailability of Internet connection
 */
object NetworkUtils : ConnectivityManager.NetworkCallback() {

    private val networkLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun getNetworkLiveData(context: Context): LiveData<Boolean> {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager.registerDefaultNetworkCallback(this)

        val network = connectivityManager.activeNetwork

        val actNetwork = connectivityManager.getNetworkCapabilities(network) ?: return networkLiveData
        val isConnected = when {
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Log.d("Network", "wifi connected")
                true
            }
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Log.d("Network", "cellular network connected")
                true
            }
            else -> {
                Log.d("Network", "internet not connected")
                false
            }
        }

        networkLiveData.postValue(isConnected)

        return networkLiveData
    }

    fun isNetworkAvailable(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager.registerDefaultNetworkCallback(this)

        val network = connectivityManager.activeNetwork

        val actNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        val isConnected = when {
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Log.d("Network", "wifi connected")
                true
            }
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Log.d("Network", "cellular network connected")
                true
            }
            else -> {
                Log.d("Network", "internet not connected")
                false
            }
        }

        return isConnected
    }

    override fun onAvailable(network: Network) {
        networkLiveData.postValue(true)
    }

    override fun onLost(network: Network) {
        networkLiveData.postValue(false)
    }
}
