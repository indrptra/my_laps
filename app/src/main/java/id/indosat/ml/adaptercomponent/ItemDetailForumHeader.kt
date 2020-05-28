package id.indosat.ml.adaptercomponent
import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.R
import id.indosat.ml.common.model.MLPrefModel
import id.indosat.ml.common.model.response.ResultDFR
import id.indosat.ml.productcontext.forum.DetailForumActivity
import id.indosat.ml.util.loadURL
import kotlinx.android.synthetic.main.item_detail_forum_header.*

open class ItemDetailForumHeader(private val data:ResultDFR,
                                 private val canEdit:Boolean,
                                 private val canDelete:Boolean,
                                 private val callBackLike:(ResultDFR)->Unit,
                                 private val callBackSubscribe: (ResultDFR) -> Unit,
                                 private val callBackEdit: (ResultDFR) -> Unit,
                                 private val callBackDelete: (ResultDFR) -> Unit,
                                 private val callBackReport: (ResultDFR) -> Unit) : Item(){
    override fun getLayout(): Int {
        return R.layout.item_detail_forum_header
    }

    var isSubscribed = false
    var isLiked = false
    var isButtonsEnabled = false
    override fun bind(viewHolder: ViewHolder, position: Int) {
        var textHTML = data.rows.message

        if(data.rows.attachment){
            for (attach in data.rows.attachments){
                textHTML += "<br><a href = \"${attach.fileurl}\">${attach.filename}</a><br>"
            }
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            viewHolder.item_dfh_content?.text = Html.fromHtml(textHTML, Html.FROM_HTML_MODE_LEGACY)
            //textHTML = Html.fromHtml(data.rows.message, Html.FROM_HTML_MODE_LEGACY).toString()
        }else{
            viewHolder.item_dfh_content?.text = Html.fromHtml(textHTML)
            //textHTML = Html.fromHtml(data.rows.message).toString()
        }
        viewHolder.item_dfh_content?.movementMethod = LinkMovementMethod.getInstance()
        viewHolder.item_dfh_createdBy?.text = data.rows.createdby
        viewHolder.item_dfh_createdAt?.text = data.rows.created
        viewHolder.item_dfh_profile_pic?.let {civ->
            civ.context?.let {ctx ->
                if(ctx is DetailForumActivity){
                    if(!ctx.isFinishing){
                        civ.loadURL(data.rows.creatorprofilepicture)
                    }
                }
            }
        }

        viewHolder.item_kf_liked?.text = data.rows.numlikes.toString()
        viewHolder.item_kf_subs?.text = data.rows.numsubscriber.toString()
        viewHolder.item_kf_reply?.text = data.rows.numreplies.toString()

        if(!canEdit) viewHolder.item_dfh_edit.visibility = View.GONE
        if(!canDelete) viewHolder.item_dfh_delete.visibility = View.GONE

        viewHolder.item_dfh_category?.text = data.rows.category
        viewHolder.item_dfh_title?.text = data.rows.name
        viewHolder.item_dfh_button_like?.isEnabled = isButtonsEnabled
        viewHolder.item_dfh_button_like?.setOnClickListener {
            callBackLike(data)
        }


        viewHolder.item_dfh_button_subscribe?.isEnabled = isButtonsEnabled
        viewHolder.item_dfh_button_subscribe?.setOnClickListener {
            callBackSubscribe(data)
        }

        if (MLPrefModel.userFullName == data.rows.createdby) {
            viewHolder.item_dfh_button_report?.visibility = View.GONE
        }

        viewHolder.item_dfh_button_report?.isEnabled = isButtonsEnabled
        viewHolder.item_dfh_button_report?.setOnClickListener {
            callBackReport(data)
        }
        viewHolder.item_dfh_edit?.setOnClickListener {
            callBackEdit(data)
        }
        viewHolder.item_dfh_delete?.setOnClickListener {
            callBackDelete(data)
        }


        viewHolder.item_dfh_top_container?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        viewHolder.item_dfh_top_container?.invalidate()
        viewHolder.item_dfh_button_subscribe?.context?.let{
            if(isSubscribed){
                viewHolder.item_dfh_button_subscribe?.setBackgroundColor(ContextCompat.getColor(it,R.color.colorLightGreen))
            }else{
                viewHolder.item_dfh_button_subscribe?.setBackgroundColor(ContextCompat.getColor(it,android.R.color.darker_gray))
            }
        }

        viewHolder.item_dfh_button_like?.context?.let{
            if(isLiked){
                viewHolder.item_dfh_button_like?.setBackgroundColor(ContextCompat.getColor(it,R.color.colorLightGreen))
            }else{
                viewHolder.item_dfh_button_like?.setBackgroundColor(ContextCompat.getColor(it,android.R.color.darker_gray))
            }

        }


    }

    fun setIsSubscribed(state: Boolean){
        isSubscribed = state
    }

    fun setIsLiked(state:Boolean){
        isLiked = state
    }

    fun toggleButtonEnabled(state:Boolean){
        isButtonsEnabled = state
    }
}