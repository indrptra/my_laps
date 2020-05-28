package id.indosat.ml.productcontext.course

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import dagger.android.AndroidInjection
import id.indosat.ml.R
import id.indosat.ml.adaptercomponent.ItemSingleWithRightCaret
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.coordinator.navigateTo
import id.indosat.ml.common.model.response.RowSubCategory
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.productcontext.search.SearchActivity
import id.indosat.ml.util.MLLog
import id.indosat.ml.util.getDisplayMetrics
import id.indosat.ml.util.loadURL
import id.indosat.ml.util.showToast
import id.indosat.ml.viewmodel.MLCourseViewModel
import kotlinx.android.synthetic.main.activity_main_course.*
import kotlinx.android.synthetic.main.content_main_course.*

class MainCourseActivity : BaseActivity() {
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
        setContentView(R.layout.activity_main_course)
        setSupportActionBar(toolbar_main_course)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_main_course?.title = ""
        supportActionBar?.title = ""
        setSearchButton()
        setAdapterAndManager()
        setHeaderImage()
        courseViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLCourseViewModel::class.java)
        courseViewModel.fromRowSorted = 0
        courseViewModel.limitRowSorted = MLConfig.LimitData
        courseViewModel.totalDataSorted = 0
        getCourse()
        getCourseSubCategories()
    }


    private fun setAdapterAndManager(){
        val llManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        main_course_subcat_rv?.layoutManager = llManager
        main_course_subcat_rv?.adapter = mainCourseAdapter
        /*val llManager = GridLayoutManager(this,2, RecyclerView.VERTICAL,false)
        main_course_rv?.layoutManager = llManager
        main_course_rv?.adapter = mainCourseAdapter
        main_course_rv?.addOnScrollListener(object : PaginationGLScrollListener(llManager){
            override fun loadMoreItems() {
                if(courseViewModel.totalDataSorted <= courseViewModel.fromRowSorted){return}
                //getCourse(courseViewModel.fromRowSorted)
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

        })*/
    }

    private fun setHeaderImage(){
        val dblWidth = getDisplayMetrics().widthPixels.toDouble()
        val height = (dblWidth * 0.45)
        main_course_header_image?.layoutParams?.height = height.toInt()
        main_course_header_image?.layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
    }

    private fun setSearchButton(){
        search_bar_button_course?.setOnClickListener {
            navigateTo<SearchActivity>()
        }
    }

    private fun goToMainCourse(sub: RowSubCategory){
        val intent = Intent(this,SubCategoriesActivity::class.java)
        intent.putExtra(courseCatIdName, sub.id)
        intent.putExtra(courseCatName, sub.name)
        intent.putExtra(courseCat, sub)
        startActivity(intent)
    }


    private fun getCourseSubCategories(){
        intent.getIntExtra(courseCatIdName,0).let {
            toggleProgress(true)
           courseViewModel.getCourseSubCategories(it){isValidToken,errorState,message,response->
               mainCourseAdapter.clear()
               toggleProgress(false)
               if(!isValidToken){
                   showToast(message)
                   navigateTo<LoginActivity>(true)
               }else{
                   if(!errorState){
                       response?.let {res->
                           res?.rows?.let {_rows->
                               if(_rows.isNotEmpty()) {
                                   mainCourseAdapter.addAll(res.rows
                                       .filter { rowFil->rowFil.visible == 1}
                                       .map{row->ItemSingleWithRightCaret(row, { rowSubCategory: RowSubCategory ->
                                            goToMainCourse(rowSubCategory)
                                       },this.applicationContext)
                                       })
                               }else{
                                   showToast(message)
                               }
                           }?:run{

                           }

                       }?:run{
                           showToast(message)
                       }
                   }else{
                       showToast(message)
                   }
               }
           }
        }
    }


    private fun getCourse(fromRow:Int=0,limitRow:Int=1){
        intent.getIntExtra(courseCatIdName,0).let {
            courseViewModel.selectedCat = it
            courseViewModel.getCategoryCourse(fromRow,limitRow){isTokenValid,isError,isThisLast,message, response->
                if(!isTokenValid){
                    showToast(message)
                    navigateTo<LoginActivity>(true)
                }else{
                    if(isError){
                        showToast(message)
                    }else{
                        response?.let {resp->
                            if(!isFinishing) {
                                main_course_header_image?.loadURL(resp.headerimage)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun toggleProgress(state:Boolean){
        if(state){
            main_course_progress?.visibility = View.VISIBLE
            main_course_subcat_rv?.visibility = View.GONE
        }else{
            main_course_progress?.visibility = View.GONE
            main_course_subcat_rv?.visibility = View.VISIBLE
        }
        /*if (fromRow == 0) {
            main_course_progress?.visibility = if (state) View.VISIBLE else {
                View.GONE
            }
            /*main_course_rv?.visibility = if (state) View.GONE else {
                View.VISIBLE
            }*/
        }else{
            /*if(state){
                main_course_bottom_progress?.visibility = View.VISIBLE
                main_course_bottom_progress?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }else{
                main_course_bottom_progress?.layoutParams?.height = 1
                main_course_bottom_progress?.visibility = View.INVISIBLE
            }
            main_course_bottom_progress?.requestLayout()
            main_course_rv?.requestLayout()*/
        }*/

    }


}
