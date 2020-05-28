package id.indosat.ml.productcontext.search

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.R
import kotlinx.android.synthetic.main.content_search.*
import kotlinx.android.synthetic.main.item_search_autocomp.*

open class ItemSearchAutoComplete(val text:String,val callback:(String)->Unit):Item(){
    override fun getLayout(): Int {
        return R.layout.item_search_autocomp
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.search_text_autocomp?.text = text
        viewHolder.search_completion_item_container?.setOnClickListener {
            callback(text)
        }

    }
}
