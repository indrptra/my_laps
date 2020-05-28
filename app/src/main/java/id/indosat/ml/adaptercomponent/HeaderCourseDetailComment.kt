package id.indosat.ml.adaptercomponent


import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.R
import id.indosat.ml.productcontext.course.CourseDetailActivity
import id.indosat.ml.util.loadURL
import kotlinx.android.synthetic.main.header_course_detail_comment_rv.*
import id.indosat.ml.common.model.MLPrefModel
open class HeaderCourseDetailComment(private val courseCommentFound:Int,val callback:()->Unit):Item(){
    override fun getLayout(): Int {
        return R.layout.header_course_detail_comment_rv
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.course_detail_header_comment_number?.text = "$courseCommentFound comments of this course"
        viewHolder.course_detail_header_comment_box?.context?.let{ctx->
            if(ctx is CourseDetailActivity){
                if(!ctx.isFinishing){
                    viewHolder.course_detail_header_comment_box_image?.loadURL(MLPrefModel.userImageLarge)
                }
            }
        }

        viewHolder.course_detail_header_comment_fake_et?.setOnClickListener {
            callback.invoke()
        }


    }
}