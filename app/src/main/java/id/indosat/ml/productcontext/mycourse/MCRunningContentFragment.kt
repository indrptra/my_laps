package id.indosat.ml.productcontext.mycourse

import android.os.Bundle

class MCRunningContentFragment:BaseMyCoursesContentFragment(){
    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            BaseMyCoursesContentFragment().apply {
                arguments = Bundle().apply {
                    putString(BaseMyCoursesContentFragment.myCoursesTabName,param1)
                }
            }
    }
}