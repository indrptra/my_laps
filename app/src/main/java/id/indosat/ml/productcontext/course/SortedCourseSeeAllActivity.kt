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
import id.indosat.ml.adaptercomponent.ItemSortedCourse
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
import kotlinx.android.synthetic.main.activity_sorted_course_see_all.*

class SortedCourseSeeAllActivity : BaseActivity() {
    companion object {
        const val titleSortCourse = "title-sorted-course"
        const val typeOfSorted = "type-of-sorted"
    }

    private lateinit var courseViewModel:MLCourseViewModel
    private var adapter = GroupAdapter<ViewHolder>()
    private lateinit var glm : GridLayoutManager
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
        setContentView(R.layout.activity_sorted_course_see_all)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        intent.getStringExtra(titleSortCourse)?.let {
            supportActionBar?.title = it
        }?:run{
            supportActionBar?.title = ""
        }
        initiateListSupport()
        courseViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLCourseViewModel::class.java)
        courseViewModel.fromRowSorted = 0
        courseViewModel.limitRowSorted = MLConfig.LimitData
        courseViewModel.totalDataSorted = 0
        getAllSortedCourse()
    }

    private var isLastPage = false
    private var isLoading = false
    private fun initiateListSupport(){
        glm = GridLayoutManager(this,2, RecyclerView.VERTICAL,false)
        sorted_all_rv?.layoutManager = glm
        sorted_all_rv?.adapter = adapter
        sorted_all_rv?.addOnScrollListener(object : PaginationGLScrollListener(glm){
            override fun loadMoreItems() {
                if(courseViewModel.totalDataSorted <= courseViewModel.fromRowSorted){return}
                getAllSortedCourse(courseViewModel.fromRowSorted)
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

        })
    }

    private fun toggleBottomProgress(state:Boolean){
        if(state){
            sorted_all_bottom_progress?.visibility = View.VISIBLE
            sorted_all_bottom_progress?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }else{
            sorted_all_bottom_progress?.layoutParams?.height = 1
            sorted_all_bottom_progress?.visibility = View.INVISIBLE
        }
        sorted_all_bottom_progress?.requestLayout()
        sorted_all_rv?.requestLayout()
    }

    private fun getAllSortedCourse(fromRow:Int=0,limitRow:Int=20){
        intent.getStringExtra(typeOfSorted)?.let {
            if(fromRow == 0){
                toggleBottomProgress(false)
                sorted_all_rv?.visibility = View.GONE
                sorted_all_progress_center?.visibility = View.VISIBLE
            }else{
                toggleBottomProgress(true)
            }
            isLoading = true
            courseViewModel.getSortedCourseLimit(it,fromRow,limitRow){isTokenValid,errorState,isThisLast,message,response->
                isLoading = false
                isLastPage = isThisLast
                toggleBottomProgress(false)
                if(fromRow == 0){
                    sorted_all_progress_center?.visibility = View.GONE
                    sorted_all_rv?.visibility = View.VISIBLE
                }
                if(!isTokenValid){
                    navigateTo<LoginActivity>(true)
                }else{
                    if(errorState){
                        showToast(message)
                    }else{
                        //process the result
                        response?.let {resp->
                            if(fromRow == 0){
                                adapter.clear()
                            }
                            val eachWidthCard = (getDisplayMetrics().widthPixels - dpToPx(32)) / 2
                            if(resp.rows.isNotEmpty()){
                                adapter.addAll(resp.rows.map { course->
                                    ItemSortedCourse(course,eachWidthCard,dpToPx(MLConfig.CardHeight)){ view, row->
                                        navigateToCourseDetail(row.id)
                                    } })
                            }
                        }


                    }
                }
            }
        }?:run {
            showToast("UNDEFINED TYPE")
        }

    }
}
