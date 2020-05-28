package id.indosat.ml.adaptercomponent

import android.os.Build
import android.text.Html
import android.view.View
import com.daimajia.swipe.SwipeLayout
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.R
import id.indosat.ml.common.model.response.RowForum
import id.indosat.ml.util.formatAbbrv
import kotlinx.android.synthetic.main.item_knowledge_forum.*

open class ItemKnowledgeForum(private val forum:RowForum,
                              private val isSlideAble:Boolean,
                              private val callback:(RowForum)->Unit,
                              private val closeCallback:(RowForum)->Unit):Item(){
    override fun getLayout(): Int {
        return R.layout.item_knowledge_forum
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.item_kf_swipelayout?.isSwipeEnabled = isSlideAble
        if(isSlideAble) {
            viewHolder.item_kf_swipelayout?.showMode = SwipeLayout.ShowMode.LayDown
            viewHolder.item_kf_bottom_wrapper?.let { bottomWrapper ->
                viewHolder.item_kf_swipelayout?.addDrag(SwipeLayout.DragEdge.Right, bottomWrapper)
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            viewHolder.item_kf_posted_by?.text = Html.fromHtml("Posted by ${forum.lastmodifiedby} on ${forum.lastmodified}",
                Html.FROM_HTML_MODE_COMPACT).toString()
            /*viewHolder.item_kf_posted_by?.text = Html.fromHtml("Posted by ${forum.name} on ${forum.lastmodified}",
                Html.FROM_HTML_MODE_COMPACT).toString()*/

            viewHolder.item_kf_title?.text = Html.fromHtml(forum.name,
                Html.FROM_HTML_MODE_COMPACT).toString()
        }else{
            viewHolder.item_kf_posted_by?.text = Html.fromHtml("Posted by ${forum.lastmodifiedby} on ${forum.lastmodified}").toString()
            //viewHolder.item_kf_posted_by?.text = Html.fromHtml("Posted by ${forum.name} on ${forum.lastmodified}").toString()
            viewHolder.item_kf_title?.text = Html.fromHtml(forum.name).toString()
        }
        viewHolder.item_kf_category?.text = forum.category
        viewHolder.item_kf_liked?.text = forum.numlikes.formatAbbrv()
        viewHolder.item_kf_subs?.text = forum.numsubscriber.formatAbbrv()
        viewHolder.item_kf_reply?.text = forum.numreplies.formatAbbrv()
        viewHolder.item_knowledge_forum_container?.setOnClickListener {
            callback(forum)
        }


        viewHolder.item_kf_close_button?.setOnClickListener {
            closeCallback(forum)
        }

    }
}