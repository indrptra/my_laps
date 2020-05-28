package id.indosat.ml.productcontext.home

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.R
open class ItemLeaderBoardHeader():Item(){
    override fun getLayout(): Int {
        return R.layout.item_leaderboard_header
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

    }
}