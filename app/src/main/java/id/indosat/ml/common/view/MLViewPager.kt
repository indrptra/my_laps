package id.indosat.ml.common.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class MLViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    private var pageEnabled: Boolean = false

    init {
        this.pageEnabled = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (this.pageEnabled) {
            super.onTouchEvent(event)
        } else false

    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.pageEnabled) {
            super.onInterceptTouchEvent(event)
        } else false

    }

    fun setPagingEnabled(enabled: Boolean) {
        this.pageEnabled = enabled
    }
}