package id.indosat.ml.adaptercomponent

import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.R
import id.indosat.ml.common.model.MLPrefModel
import id.indosat.ml.common.model.response.ResultForumComment
import id.indosat.ml.common.model.response.RowForumComment
import id.indosat.ml.util.loadURL
import kotlinx.android.synthetic.main.item_comment_forum.*

open class ItemCommentForum(private val rowForumComment: RowForumComment,private val canEdit: Boolean, private val canDelete: Boolean, private val arrPos:Int, private val data: ResultForumComment,
                            private val callBackEdit:(ResultForumComment)->Unit, private val callBackDelete:(ResultForumComment)->Unit, private val callBackReport:(ResultForumComment)->Unit,
                            private val callBackLike:(ResultForumComment)->Unit):Item(){
    override fun getLayout(): Int {
        return R.layout.item_comment_forum
    }
    var isLiked = false
    var isButtonsEnabled = false
    override fun bind(viewHolder: ViewHolder, position: Int) {
        var textHTML = rowForumComment.message
        if(rowForumComment.attachment && rowForumComment.attachments.isNotEmpty()){
            for(attachment in rowForumComment.attachments){
                textHTML += "<br><a href = \"${attachment.fileurl}\">${attachment.filename}</a><br>"
            }
        }
        if(arrPos - 1 == 0){
            viewHolder.item_comment_forum_header_rep_container?.visibility = View.VISIBLE
        }else{
            viewHolder.item_comment_forum_header_rep_container?.visibility = View.GONE
        }
        viewHolder.item_comment_forum_name?.text = rowForumComment.postby
        viewHolder.item_comment_forum_avatar?.loadURL(rowForumComment.postprofileimage)
        viewHolder.item_comment_forum_date?.text = rowForumComment.created
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            viewHolder.item_comment_forum_content?.text = Html.fromHtml(textHTML, Html.FROM_HTML_MODE_LEGACY)
        }else{
            viewHolder.item_comment_forum_content?.text = Html.fromHtml(textHTML)
        }
        viewHolder.item_comment_forum_content?.movementMethod = LinkMovementMethod.getInstance()
        if(!canEdit)viewHolder.item_comment_edit.visibility = View.GONE
        if(!canDelete)viewHolder.item_comment_delete.visibility = View.GONE
        viewHolder.item_comment_edit?.setOnClickListener {
            callBackEdit(data)
        }
        viewHolder.item_comment_delete?.setOnClickListener {
            callBackDelete(data)
        }

        viewHolder.item_comment_like?.setOnClickListener {
            callBackLike(data)
        }

        if (MLPrefModel.userId == rowForumComment.userid) {
            viewHolder.fl_report?.visibility = View.GONE
            viewHolder.item_comment_report?.visibility = View.GONE
        }

        viewHolder.item_comment_report?.setOnClickListener{
            callBackReport(data)
        }

    }

    fun setIsLiked(state:Boolean){
        isLiked = state
    }

    fun toggleButtonEnabled(state:Boolean){
        isButtonsEnabled = state
    }
}