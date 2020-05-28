package id.indosat.ml.util

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.showToast(message:String,duration:Int = Toast.LENGTH_SHORT){
    activity?.let {
        Toast.makeText(it,message, duration).show()
    }

}