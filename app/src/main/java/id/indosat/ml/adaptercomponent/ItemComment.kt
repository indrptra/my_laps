package id.indosat.ml.adaptercomponent

import android.os.Build
import android.text.Html
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.R
import id.indosat.ml.common.model.response.RowComments
import id.indosat.ml.productcontext.course.CourseDetailActivity
import id.indosat.ml.util.loadURL
import kotlinx.android.synthetic.main.item_comment.*

open class ItemComment(private val row:RowComments):Item(){
    override fun getLayout(): Int {
        return R.layout.item_comment
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.comment_container?.context?.let {
            if(it is CourseDetailActivity){
                if(!it.isFinishing){
                    viewHolder.comment_avatar?.loadURL(row.avatar)
                }
            }
        }
        viewHolder.comment_name_date?.text = "${row.fullname}, ${row.postdate}"
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            viewHolder.comment_content?.text = Html.fromHtml(row.content, Html.FROM_HTML_MODE_COMPACT).toString()
        }else{
            viewHolder.comment_content?.text = Html.fromHtml(row.content).toString()
        }

    }
}