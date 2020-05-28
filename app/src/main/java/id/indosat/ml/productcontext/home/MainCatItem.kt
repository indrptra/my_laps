package id.indosat.ml.productcontext.home

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.R
import id.indosat.ml.common.model.response.RowMainMenu
import id.indosat.ml.common.model.response.RowSubCategory
import id.indosat.ml.util.MLLog
import kotlinx.android.synthetic.main.item_main_cat_menu.*
import kotlinx.android.synthetic.main.item_sub_item.*

open class MainCatItem(val rmm:RowMainMenu,val callback:(RowMainMenu)->Unit):Item(){
    override fun getLayout(): Int {
        return R.layout.item_main_cat_menu
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.menu_cat_btn?.text = rmm.name
        viewHolder.menu_cat_btn?.setOnClickListener {
            MLLog.showLog("MAIN CAT ITEM",rmm.name)
            callback(rmm)
        }
    }
}

open class SubCatItem(val rmm:RowSubCategory,val callback:(RowSubCategory)->Unit):Item(){
    override fun getLayout(): Int {
        return R.layout.item_sub_item
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.subcat_item_name?.text = rmm.name
        viewHolder.subcat_item_container?.setOnClickListener {
            callback(rmm)
        }
    }
}