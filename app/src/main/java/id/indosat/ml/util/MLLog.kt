package id.indosat.ml.util

import android.util.Log

object MLLog{
    fun showLog(withTag:String,andMessage:String){
        Log.e(withTag,andMessage)
    }
}