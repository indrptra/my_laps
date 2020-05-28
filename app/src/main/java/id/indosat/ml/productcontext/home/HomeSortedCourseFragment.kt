package id.indosat.ml.productcontext.home


import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

import id.indosat.ml.R
import id.indosat.ml.base.BaseFragment
import id.indosat.ml.common.ml_enum.MLESortCourseType
import id.indosat.ml.common.model.response.ResultSortedCourse
import kotlinx.android.synthetic.main.fragment_home_sorted_course.*
import id.indosat.ml.adaptercomponent.ItemSortedCourse
import id.indosat.ml.common.coordinator.navigateToCourseDetail
import id.indosat.ml.util.dpToPx
import id.indosat.ml.util.getDisplayMetrics
import id.indosat.ml.util.MLLog
import id.indosat.ml.common.view.ItemSeeAll
import id.indosat.ml.productcontext.course.SortedCourseSeeAllActivity
import id.indosat.ml.core.config.MLConfig
import kotlinx.android.synthetic.main.content_note.*

class HomeSortedCourseFragment : BaseFragment() {
    private var typeSort:MLESortCourseType?=null
    private var typeResult:ResultSortedCourse?=null
    //private lateinit var courseViewModel: MLCourseViewModel
    private val adapter = GroupAdapter<ViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        classTag = "HOMESORTED COURSE"
        arguments?.let {
            it.get(HomeSortedCourseFragment.typeSortName)?.let {_sortType->
                if(_sortType is MLESortCourseType){
                    typeSort = _sortType
                    classTag = _sortType.name
                }
            }

            it.get(HomeSortedCourseFragment.typeResultName)?.let {_result->
                if(_result is ResultSortedCourse){
                    typeResult = _result
                }
            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_sorted_course, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var emptyReason = ""
        activity?.let {act->
            sorted_course_rv?.visibility = View.VISIBLE
            sorted_course_type?.visibility = View.VISIBLE
            frag_home_content_note?.visibility = View.GONE
            frag_home_content_note_container?.visibility = View.GONE

            typeSort?.let {_typeSort->
                frag_home_content_note_container?.setOnClickListener {
                    if(act is HomeActivity){
                        act.getSortedCourseData(_typeSort)
                    }
                }
                when(_typeSort){
                    MLESortCourseType.POPULAR->{
                        sorted_course_type?.text = "MOST POPULAR COURSES"
                        emptyReason = "Cannot load most popular courses"
                    }
                    MLESortCourseType.TOPRATED->{
                        sorted_course_type?.text = "TOP RATED COURSES"
                        emptyReason = "Cannot load top rated courses"
                    }
                    MLESortCourseType.LATEST->{
                        emptyReason = "Cannot load latest courses"
                        sorted_course_type?.text = "LATEST COURSES"
                    }
                }
                val eachWidthCard = (act.getDisplayMetrics().widthPixels - act.dpToPx(40)) / 2

                typeResult?.let {rsc->
                    //adapter.addAll(rsc.rows.map {ItemSortedCourse(it,eachWidthCard,act.dpToPx(224)){row->
                    //val eachHeight = (eachWidthCard.toDouble() * 0.48).toInt()
                    //adapter.addAll(rsc.rows.map {ItemSortedCourse(it,eachWidthCard,act.dpToPx(eachHeight)){row->


                    if(rsc.rows.isNotEmpty()){
                        adapter.addAll(rsc.rows.map{ItemSortedCourse(it,eachWidthCard,act.dpToPx(MLConfig.CardHeight)){view, row->
                            when(view.id){
                                R.id.sorted_course_main_container->{
                                    MLLog.showLog(classTag,row.fullname!!)
                                    act.navigateToCourseDetail(row.id)
                                }
                                R.id.sc_btn_start->{
                                    act.navigateToCourseDetail(courseId = row.id, directStart = true)
                                }
                            }
                        } })
                        adapter.add(ItemSeeAll(_typeSort,eachWidthCard,act.dpToPx(MLConfig.CardHeight)){
                        //adapter.add(ItemSeeAll(_typeSort,eachWidthCard,act.dpToPx(eachHeight)){
                            //MLLog.showLog(classTag,"SEE ALL OF $_typeSort")
                            val seeAllIntent = Intent(act,SortedCourseSeeAllActivity::class.java)
                            seeAllIntent.putExtra(SortedCourseSeeAllActivity.titleSortCourse,sorted_course_type?.text ?: "")
                            seeAllIntent.putExtra(SortedCourseSeeAllActivity.typeOfSorted,_typeSort.type)
                            act.startActivity(seeAllIntent)

                        })
                        val llm = LinearLayoutManager(
                            act,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        sorted_course_rv?.layoutManager = llm
                        sorted_course_rv?.adapter = adapter
                        home_sorted_frag_container?.requestLayout()
                    }else{
                        MLLog.showLog("ASY","asu")
                    }

                }?:run{
                    sorted_course_rv?.visibility = View.GONE
                    sorted_course_type?.visibility = View.GONE
                    frag_home_content_note_container?.visibility = View.VISIBLE
                    frag_home_content_note?.visibility = View.VISIBLE
                    content_note_text?.text = emptyReason
                }

            }
        }
    }


    companion object {
        const val typeSortName = "type-sort-name"
        const val typeResultName = "type-result-name"
        @JvmStatic
        fun newInstance(type:MLESortCourseType,response: ResultSortedCourse?) =
            HomeSortedCourseFragment().apply {
                arguments = Bundle().apply {

                }
                arguments?.putSerializable(HomeSortedCourseFragment.typeSortName,type)
                arguments?.putSerializable(HomeSortedCourseFragment.typeResultName,response)
            }
    }
}
