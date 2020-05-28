package id.indosat.ml.productcontext.notification

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import dagger.android.AndroidInjection
import id.indosat.ml.adaptercomponent.ItemNotification
import id.indosat.ml.adaptercomponent.PaginationGLScrollListener
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.coordinator.navigateTo
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.util.showToast
import id.indosat.ml.viewmodel.MLNotificationViewModel
import kotlinx.android.synthetic.main.content_notification.*

class NotificationActivity : BaseActivity() {
    lateinit var notificationViewModel: MLNotificationViewModel
    private val notificationAdapter = GroupAdapter<ViewHolder>()
    private var isLastPage = false
    private var isLoading = false

    override fun onDestroy() {
        super.onDestroy()
        notificationViewModel.clear()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(id.indosat.ml.R.layout.activity_notification)
        setAdapterAndManager()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "NOTIFICATION"
        supportActionBar?.elevation = 0f

        notificationViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLNotificationViewModel::class.java)
        notificationViewModel.fromRowSorted = 0
        notificationViewModel.limitRowSorted = MLConfig.LimitData
        notificationViewModel.totalDataSorted = 0

        getNofitications()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0,0)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let{
            if(it.itemId == android.R.id.home){
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setAdapterAndManager(){
        val llManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)
        notification_rv?.layoutManager = llManager
        notification_rv?.adapter = notificationAdapter
        notification_rv?.addOnScrollListener(object : PaginationGLScrollListener(llManager) {
            override fun loadMoreItems() {
                if (notificationViewModel.totalDataSorted <= notificationViewModel.fromRowSorted) {
                    return
                }
                getNofitications(notificationViewModel.fromRowSorted)
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

        })

    }

    private fun toggleProgress(state:Boolean){
        if(state){
            sorted_all_bottom_progress?.visibility = View.VISIBLE
            sorted_all_bottom_progress?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }else{
            sorted_all_bottom_progress?.layoutParams?.height = 1
            sorted_all_bottom_progress?.visibility = View.INVISIBLE
        }
        sorted_all_bottom_progress?.requestLayout()
        notification_rv?.requestLayout()
    }

    private fun getNofitications(fromRow:Int=0,limitRow:Int= MLConfig.LimitData){

        if(fromRow == 0){
            toggleProgress(false)
            notification_rv?.visibility = View.GONE
            sorted_all_progress_center?.visibility = View.VISIBLE
        }else{
            toggleProgress(true)
        }

        notificationViewModel.getNotifications(fromRow,limitRow){isTokenValid,isError,isThisLast,message, response->

            isLoading = false
            isLastPage = isThisLast
            toggleProgress(false)
            if(fromRow == 0){
                sorted_all_progress_center?.visibility = View.GONE
                notification_rv?.visibility = View.VISIBLE
            }

            if(!isTokenValid){
                showToast(message)
                navigateTo<LoginActivity>(true)
            }else{
                if(!isError){
                    response?.let {res->

                        if(fromRow == 0){
                            notificationAdapter.clear()
                        }

                        res.rows?.let { _rows->
                            if(_rows.isNotEmpty()) {
                                notificationAdapter.addAll(res.rows
                                    .map{row->
                                        ItemNotification(row, this.applicationContext){
                                            navigateTo<NotificationDetailActivity>(false, row,"notification-detail")
                                        }
                                    })
                            }else{
                                showToast(message)
                            }
                        }?:run{

                        }

                    }?:run{
                        showToast(message)
                    }
                }else{
                    showToast(message)
                }
            }
        }
    }

}
