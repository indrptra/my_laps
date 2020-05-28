package id.indosat.ml.productcontext.mycourse

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import id.indosat.ml.R
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.view.MLTabAdapter
import kotlinx.android.synthetic.main.activity_my_course_main.*

class MyCourseMainActivity : BaseActivity() {
    companion object {
        const val myCoursesTabName = "my-courses-tab-name"
    }

    private lateinit var tabAdapter:MLTabAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course_main)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "MY COURSES"
        supportActionBar?.elevation = 0f
        setTabLayout()
    }


    private fun setTabLayout(){
        mycourses_view_pager?.setPagingEnabled(true)
        mycourses_tab_layout?.let {
            it.addTab(it.newTab().setText("RUNNING COURSES"))
            it.addTab(it.newTab().setText("FUTURE COURSES"))
            it.addTab(it.newTab().setText("COMPLETED COURSES"))
            it.tabGravity = TabLayout.GRAVITY_FILL
            tabAdapter = MLTabAdapter(supportFragmentManager,it.tabCount)
            mycourses_view_pager?.adapter = tabAdapter
            mycourses_view_pager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(it))
        }

        mycourses_tab_layout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    mycourses_view_pager?.currentItem = it.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }
}
