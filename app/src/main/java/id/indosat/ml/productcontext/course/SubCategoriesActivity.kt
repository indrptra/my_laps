package id.indosat.ml.productcontext.course

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import dagger.android.AndroidInjection
import id.indosat.ml.R
import id.indosat.ml.adaptercomponent.ItemSortedCourseGroup
import id.indosat.ml.adaptercomponent.PaginationGLScrollListener
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.coordinator.navigateTo
import id.indosat.ml.common.coordinator.navigateToCourseDetail
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.util.MLLog
import id.indosat.ml.util.dpToPx
import id.indosat.ml.util.getDisplayMetrics
import id.indosat.ml.util.showToast
import id.indosat.ml.viewmodel.MLCourseViewModel
import kotlinx.android.synthetic.main.activity_sub_categories.*
import kotlinx.android.synthetic.main.content_note.*

class SubCategoriesActivity : BaseActivity() {
    companion object {
        const val courseCatIdName = "course-category-id-name"
        const val courseCatName = "course-category-name"
        const val courseCat = "course-category"
    }
    private lateinit var courseViewModel: MLCourseViewModel
    private val mainCourseAdapter = GroupAdapter<ViewHolder>()
    override fun onDestroy() {
        super.onDestroy()
        courseViewModel.clear()
        try {
            generalViewModel.clear()
        }catch (e:Exception){
            MLLog.showLog(classTag,e.localizedMessage)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_categories)
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        intent.getStringExtra(courseCatName).let {
            supportActionBar?.title = it
        }
        setAdapterAndManager()
        courseViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLCourseViewModel::class.java)
        courseViewModel.fromRowSorted = 0
        courseViewModel.limitRowSorted = MLConfig.LimitData
        courseViewModel.totalDataSorted = 0
        getCourse()
    }

    private fun setAdapterAndManager(){
        val llManager = GridLayoutManager(this,1, RecyclerView.VERTICAL,false)
        main_course_rv?.layoutManager = llManager
        main_course_rv?.adapter = mainCourseAdapter
        main_course_rv?.addOnScrollListener(object : PaginationGLScrollListener(llManager){
            override fun loadMoreItems() {
                if(courseViewModel.totalDataSorted <= courseViewModel.fromRowSorted){return}
                getCourse(courseViewModel.fromRowSorted)
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

        })
    }




    private var isLastPage = false
    private var isLoading = false
    private fun getCourse(fromRow:Int=0,limitRow:Int= MLConfig.LimitData){
        intent.extras.getInt(courseCatIdName,-99).let {
            courseViewModel.selectedCat = it
            toggleProgress(fromRow,true)
            isLoading = true
            courseViewModel.getCategoryCourse(fromRow,1000){isTokenValid,isError,isThisLast,message, response->
                toggleProgress(fromRow,false)
                isLoading = false
                isLastPage = isThisLast
//                response?.rows?.first().let {rc->
//                    supportActionBar?.title = rc?.categoryname ?: "-"
//                }
                if(!isTokenValid){
                    showToast(message)
                    navigateTo<LoginActivity>(true)
                }else{
                    if(isError){
                        subcat_content_note?.visibility = View.VISIBLE
                        content_note_text?.text = message
                        main_course_rv?.visibility = View.GONE
                        supportActionBar?.title = "Cannot load category"
                        //showToast(message)
                    }else{
                        response?.let {resp->
                            if(fromRow == 0){
                                mainCourseAdapter.clear()
                            }
                            if(resp.rows.isNotEmpty()){
                                val eachWidthCard = (getDisplayMetrics().widthPixels - dpToPx(32)) / 2
                                val grouped = resp.rows.groupBy { g -> g.categoryname }

                                supportActionBar?.title = resp.rows[0].categoryname

                                mainCourseAdapter.addAll(
                                    grouped.map { course ->
                                        ItemSortedCourseGroup(course.key!!,course.value, eachWidthCard, this.applicationContext) {view, row->
                                            navigateToCourseDetail(row.id)
                                        }
                                    }
                                )

                            }else{
                                subcat_content_note?.visibility = View.VISIBLE
                                content_note_text?.text = "EMPTY RESULT"
                                main_course_rv?.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun toggleProgress(fromRow: Int=0,state:Boolean){
        if (fromRow == 0) {
            main_course_center_progress?.visibility = if (state) View.VISIBLE else {
                View.GONE
            }

            main_course_rv?.visibility = if (state) View.GONE else {
                View.VISIBLE
            }



        }else{
            if(state){
                main_course_bottom_progress?.visibility = View.VISIBLE
                main_course_bottom_progress?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }else{
                main_course_bottom_progress?.layoutParams?.height = 1
                main_course_bottom_progress?.visibility = View.INVISIBLE
            }
            main_course_bottom_progress?.requestLayout()
            main_course_rv?.requestLayout()
        }

    }
}
