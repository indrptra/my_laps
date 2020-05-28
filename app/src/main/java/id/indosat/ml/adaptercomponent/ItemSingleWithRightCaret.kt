package id.indosat.ml.adaptercomponent

import android.animation.LayoutTransition
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.R
import id.indosat.ml.common.model.response.RowSubCategory
import kotlinx.android.synthetic.main.item_subcategories_course.*


open class ItemSingleWithRightCaret(
    private val row: RowSubCategory,
    private val callback: (RowSubCategory) -> Unit,
    val context: Context
):Item(){
    override fun getLayout(): Int {
        return R.layout.item_subcategories_course
    }

    private var stateExpanded = "stateExpanded"

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.extras[stateExpanded] = false
        viewHolder.subcat_name?.text = row.name
        viewHolder.subcat_name?.setOnClickListener {
            callback(row)
        }

        val mainCatAdapter = GroupAdapter<com.xwray.groupie.ViewHolder>()

        if (row.subcats != null && row.subcats!!.isNotEmpty()) {
            mainCatAdapter.addAll(row.subcats!!.filter { rowFil->rowFil.visible == 1}
                .map{row->ItemSingleWithRightCaret(row, { rowSubCategory: RowSubCategory ->
                    callback(rowSubCategory)
                },context)
            })
            setMenuExpandableAction(viewHolder)
        } else {
            viewHolder.csub_cat_arrow_image?.visibility = View.GONE
        }

        val mainLLManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewHolder.sub_categories_rv?.setHasFixedSize(true)
        viewHolder.sub_categories_rv?.layoutManager = mainLLManager!!
        viewHolder.sub_categories_rv?.adapter = mainCatAdapter
        viewHolder.sub_categories_rv?.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)
        viewHolder.sub_categories_rv.setItemViewCacheSize(100)
    }

    private fun setMenuExpandableAction(viewHolder: ViewHolder){
        toggleRV(viewHolder)
        viewHolder.csub_cat_arrow_image?.setOnClickListener {
            val state = viewHolder.extras[stateExpanded] as Boolean
            if(!state){
                if(row.subcats != null && row.subcats!!.isNotEmpty()){
                    viewHolder.extras[stateExpanded] = true
                    viewHolder.csub_cat_arrow_image?.animate()?.rotation(90f)?.start()
                }
            }else{
                viewHolder.extras[stateExpanded] = false
                viewHolder.csub_cat_arrow_image?.animate()?.rotation(0f)?.start()
            }
            toggleRV(viewHolder)
        }
    }

    private fun toggleRV(viewHolder: ViewHolder){
        val lParams = viewHolder.sub_categories_rv?.layoutParams
        val state = viewHolder.extras[stateExpanded] as Boolean
        lParams?.height = if(state) ViewGroup.LayoutParams.WRAP_CONTENT else{0}
        viewHolder.sub_categories_rv?.layoutParams = lParams
    }
}