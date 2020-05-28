package id.indosat.ml.util

import android.content.Context
import android.net.ConnectivityManager

object DetectConnection {
    fun checkInternetConnection(context: Context): Boolean {
        val conMan = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return (conMan.activeNetworkInfo != null
                && conMan.activeNetworkInfo.isAvailable
                && conMan.activeNetworkInfo.isConnected)
    }
}