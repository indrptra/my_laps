package id.indosat.ml.adaptercomponent

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.R
open class HeaderCourseDetailModule:Item(){
    override fun getLayout(): Int {
        return R.layout.item_module_course_detail_header
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

    }
}