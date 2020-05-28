package id.indosat.ml.productcontext.home

import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.R
import id.indosat.ml.common.model.response.RowLeaderBoard
import kotlinx.android.synthetic.main.item_leaderboard_content.*
import java.text.NumberFormat
import id.indosat.ml.common.model.MLPrefModel
open class ItemLeaderBoardContent(private val row:RowLeaderBoard):Item(){
    override fun getLayout(): Int {
        return R.layout.item_leaderboard_content
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itlb_number?.text = "${row.number}"
        viewHolder.itlb_name?.text = row.fullname
        viewHolder.itlb_points?.text = NumberFormat.getIntegerInstance().format(row.points)
        viewHolder.itlb_container?.context?.let {
            if( MLPrefModel.userIdEmployee.isNotEmpty() && row.username == MLPrefModel.userIdEmployee){
                viewHolder.itlb_container?.setBackgroundColor(ContextCompat.getColor(it,R.color.colorPrimary))
            }else{
                viewHolder.itlb_container?.setBackgroundColor(ContextCompat.getColor(it,android.R.color.transparent))
            }
        }

    }
}