package id.indosat.ml.common.view

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import id.indosat.ml.common.ml_enum.MLEMyCourse
import id.indosat.ml.productcontext.mycourse.MCCompletedContentFragment
import id.indosat.ml.productcontext.mycourse.MCEmptyFragment
import id.indosat.ml.productcontext.mycourse.MCFutureContentFragment
import id.indosat.ml.productcontext.mycourse.MCRunningContentFragment

class MLTabAdapter(fm:FragmentManager,private var tabCount:Int):FragmentPagerAdapter(fm){
    override fun getItem(position: Int): Fragment {
        when(position){
            0->return MCRunningContentFragment.newInstance(MLEMyCourse.RUNNING.type)
            1->return MCFutureContentFragment.newInstance(MLEMyCourse.FUTURE.type)
            2->return MCCompletedContentFragment.newInstance(MLEMyCourse.COMPLETED.type)
            else-> return MCEmptyFragment.newInstance(MLEMyCourse.EMPTY.type)
        }
    }
    /*override fun getItem(position: Int): Fragment? {

    }*/

    override fun getCount(): Int {
        return tabCount
    }

    private var mCurrentFragment: Fragment? = null

    fun getCurrentFragment(): Fragment? {
        return mCurrentFragment
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        if(getCurrentFragment() !== `object`){
            mCurrentFragment = `object` as Fragment
        }
        super.setPrimaryItem(container, position, `object`)
    }
}