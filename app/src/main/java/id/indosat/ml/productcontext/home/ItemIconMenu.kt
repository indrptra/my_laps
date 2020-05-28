package id.indosat.ml.productcontext.home

import android.content.Context
import android.view.ViewGroup
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.common.model.response.RowMainMenu
import id.indosat.ml.R
import id.indosat.ml.util.loadURL
import kotlinx.android.synthetic.main.item_icon_menu.*

open class ItemIconMenu (private val rmm: RowMainMenu,private val eachImageWidth:Int,
                         val callback:(RowMainMenu)->Unit):Item(){
    override fun getLayout(): Int {
        return R.layout.item_icon_menu
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        /*if(eachImageWidth > 16){
            viewHolder.item_menu_home_button?.layoutParams?.width =  eachImageWidth-16
        } else{
            viewHolder.item_menu_home_button?.layoutParams?.width = eachImageWidth
        }*/
        viewHolder.item_menu_home_button?.layoutParams?.width = eachImageWidth
        viewHolder.item_menu_home_button?.layoutParams?.height = eachImageWidth//ViewGroup.LayoutParams.WRAP_CONTENT
        viewHolder.item_menu_home_button?.requestLayout()
        viewHolder.item_menu_home_button?.loadURL(rmm.iconurl)
        viewHolder.item_menu_icon_title?.text = rmm.name.replace(" ","\n")
        viewHolder.item_menu_container?.setOnClickListener {
            callback(rmm)
        }
    }


}