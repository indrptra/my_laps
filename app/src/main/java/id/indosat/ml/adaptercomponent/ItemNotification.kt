package id.indosat.ml.adaptercomponent

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.R
import id.indosat.ml.common.model.response.Notification
import id.indosat.ml.util.TimeAgo
import kotlinx.android.synthetic.main.item_notification.*
import java.text.SimpleDateFormat

open class ItemNotification(private val row: Notification, private val context: Context,
                                    private val callback:(Notification)->Unit): Item(){

    override fun getLayout(): Int {
        return R.layout.item_notification
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val date = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(row.created!!)
        val timeAgo = TimeAgo(this.context)
        val result = timeAgo.getTimeAgo(date!!)
        viewHolder.tv_message?.text = row.subject
        viewHolder.tv_timestamp?.text = result!!

        if (row.isRead!!) {
            viewHolder.tv_message.setTypeface(null, Typeface.NORMAL)
        } else {
            viewHolder.tv_message.setTypeface(null, Typeface.BOLD)
        }

        viewHolder.message_container?.setOnClickListener {
            callback(row)
            viewHolder.tv_message.setTypeface(null, Typeface.NORMAL)
        }
    }
}