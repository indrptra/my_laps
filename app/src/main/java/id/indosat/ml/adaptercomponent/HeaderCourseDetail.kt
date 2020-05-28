package id.indosat.ml.adaptercomponent

import android.os.Build
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.R
import id.indosat.ml.common.model.response.RowsCourseDetail
import id.indosat.ml.productcontext.course.CourseDetailActivity
import id.indosat.ml.util.dpToPx
import kotlinx.android.synthetic.main.header_course_detail_rv.*
import java.text.NumberFormat
import id.indosat.ml.common.ml_enum.MLECourseDetailModuleType
open class HeaderCourseDetail(val row:RowsCourseDetail,
                              val callback:(RowsCourseDetail)->Unit,
                              val ratingCallback:(RowsCourseDetail)->Unit):Item(){
    override fun getLayout(): Int {
        return R.layout.header_course_detail_rv
    }

    private var caretStateExpand = false
    var contentStartButtonContainer:LinearLayout?=null
    var thisViewHolder:ViewHolder?=null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        thisViewHolder = viewHolder
        contentStartButtonContainer = viewHolder.content_course_normal_start_container
        viewHolder.content_course_title?.text = row.fullname
        viewHolder.content_course_category?.text = row.categoryname
        viewHolder.content_course_viewer_count?.text = NumberFormat.getIntegerInstance().format(row.enrolments)
        viewHolder.content_course_star_count?.text = "${row.rating}"
        if(!row.isScorm){
            viewHolder.content_course_start_button_normal?.visibility = View.GONE
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            viewHolder.content_course_summary?.text = Html.fromHtml(row.summary,Html.FROM_HTML_MODE_COMPACT).toString()
        }else{
            viewHolder.content_course_summary?.text = Html.fromHtml(row.summary).toString()
        }
        viewHolder.content_course_carret_button?.setOnClickListener {
            caretStateExpand = !caretStateExpand
            if(caretStateExpand){
                it.animate().rotation(180f).setInterpolator(AccelerateDecelerateInterpolator()).start()
                viewHolder.content_course_summary?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }else{
                it.animate().rotation(0f).setInterpolator(AccelerateDecelerateInterpolator()).start()
                viewHolder.header_course_detail_container?.context?.let {ctx->
                    if(ctx is CourseDetailActivity){
                        viewHolder.content_course_summary?.layoutParams?.height = ctx.dpToPx(36)
                    }
                }

            }
            viewHolder.content_course_summary?.requestLayout()
        }
        viewHolder.content_course_start_button_normal?.setOnClickListener {
            callback(row)
        }

        viewHolder.content_course_rating_container?.setOnClickListener {
            ratingCallback(row)
        }

    }

    fun reloadRating(){
        thisViewHolder?.content_course_star_count?.text = "${row.rating}"
    }
}