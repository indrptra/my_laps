package id.indosat.ml.adaptercomponent

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arlib.floatingsearchview.util.Util.dpToPx
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.R
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.model.response.RowCourse
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.util.loadURL
import kotlinx.android.synthetic.main.item_sorted_course.*
import kotlinx.android.synthetic.main.item_sorted_course_group.*

open class ItemSortedCourse(private val rsc:RowCourse,
                            private val containerWidth:Int,
                            private val containerHeight:Int,
                            private val isItemContinue:Boolean = false,
                            private val callback:(view:View, RowCourse)->Unit
                            ):Item(){
    override fun getLayout(): Int {
        return R.layout.item_sorted_course
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.sorted_course_main_container?.layoutParams?.width = containerWidth
        viewHolder.sorted_course_main_container?.layoutParams?.height = containerHeight
        viewHolder.sorted_course_main_container?.context?.let {
            if(it is BaseActivity){
                if(!it.isFinishing){
                    if (rsc.imagefileurl != null) viewHolder.sc_image?.loadURL(rsc.imagefileurl!!)
                }
            }
        }
        viewHolder.sc_title?.text = rsc.fullname
        viewHolder.sc_star_rating?.text = "${rsc.rating}"
        viewHolder.sc_user_view?.text = "${rsc.enrolments}"
        viewHolder.sorted_course_main_container?.setOnClickListener {
            callback(it, rsc)
        }

        if(isItemContinue){
            viewHolder.sc_btn_start?.text = "CONTINUE LEARNING"
        }else{
            viewHolder.sc_btn_start?.text = "START LEARNING"
        }
        if (rsc.course_type.equals("completed")) {
            viewHolder.sc_btn_start?.text = "COMPLETED"
        }
        viewHolder.sc_btn_start.setOnClickListener {
            callback(it, rsc)
        }
    }
}

open class ItemSortedCourseGroup(private val catName: String,
                                 private val items: List<RowCourse>,
                                 private val width:Int,
                                 private val context: Context,
                                 private val callback:(view:View, RowCourse)->Unit) :Item() {

    private val mainCourseAdapter = GroupAdapter<com.xwray.groupie.ViewHolder>()

    override fun getLayout(): Int {
        return R.layout.item_sorted_course_group
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.tv_category_name?.text = catName
        mainCourseAdapter.clear()
        mainCourseAdapter.addAll(items.map { course->
            ItemSortedCourse(course,width,dpToPx(MLConfig.CardHeight)){ view, row->
                callback(view, row)
            } })

        val llManager = GridLayoutManager(context,2, RecyclerView.VERTICAL,false)
        viewHolder.main_course_rv_group?.layoutManager = llManager
        viewHolder.main_course_rv_group?.adapter = mainCourseAdapter
    }
}