package id.indosat.ml.productcontext.home

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.R
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.model.response.ResultStaticSlider
import id.indosat.ml.util.MLLog
import id.indosat.ml.util.loadURL
import kotlinx.android.synthetic.main.item_banner.*

open class StaticSliderItem(val rmm: ResultStaticSlider,
                            private val containerWidth:Int,
                            private val containerHeight:Int,
                            val callback:(ResultStaticSlider)->Unit): Item(){
    override fun getLayout(): Int {
        return R.layout.item_banner
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.banner_container?.layoutParams?.width = containerWidth
        viewHolder.banner_container?.layoutParams?.height = containerHeight
        viewHolder.banner_container?.context?.let {
            if(it is BaseActivity){
                if(!it.isFinishing){
                    if (rmm.image != null) viewHolder.iv_banner?.loadURL(rmm.image!!)
                }
            }
        }

        viewHolder.banner_container?.setOnClickListener {
            MLLog.showLog("SLIDER ITEM",rmm.name)
            callback(rmm)
        }

        viewHolder.iv_banner?.setOnClickListener {
            MLLog.showLog("SLIDER ITEM",rmm.name)
            callback(rmm)
        }
    }
}