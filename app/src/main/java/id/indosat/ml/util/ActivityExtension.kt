package id.indosat.ml.util

import android.app.Activity
import android.util.DisplayMetrics
import android.widget.Toast


fun Activity.getDisplayMetrics(): DisplayMetrics {
    val displayMetrics = DisplayMetrics()
    windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    return displayMetrics
}

fun Activity.dpToPx(dp: Int): Int {
    val density = resources
        .displayMetrics
        .density
    return Math.round(dp.toFloat() * density)
}

fun Activity.showToast(message:String,duration:Int = Toast.LENGTH_SHORT){
    Toast.makeText(this,message, duration).show()
}