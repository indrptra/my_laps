package id.indosat.ml.common.view

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.R
import id.indosat.ml.common.ml_enum.MLESortCourseType
import kotlinx.android.synthetic.main.item_see_all.*
import kotlinx.android.synthetic.main.item_sorted_course.*

open class ItemSeeAll(private val sortedType:MLESortCourseType?,
                      private val containerWidth:Int,
                      private val containerHeight:Int,
                      private val callback:(MLESortCourseType?)->Unit):Item(){
    override fun getLayout(): Int {
        return R.layout.item_see_all
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.item_see_all_container?.layoutParams?.width = containerWidth
        viewHolder.item_see_all_container?.layoutParams?.height = containerHeight
        viewHolder.item_see_all_container?.setOnClickListener {
            callback(sortedType)
        }
    }
}