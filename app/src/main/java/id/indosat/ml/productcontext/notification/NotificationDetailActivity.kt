package id.indosat.ml.productcontext.notification

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.ViewModelProviders
import dagger.android.AndroidInjection
import id.indosat.ml.R
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.coordinator.navigateTo
import id.indosat.ml.common.model.response.Notification
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.util.showToast
import id.indosat.ml.viewmodel.MLNotificationViewModel
import kotlinx.android.synthetic.main.content_notification_detail.*

class NotificationDetailActivity : BaseActivity() {
    lateinit var notificationViewModel: MLNotificationViewModel

    override fun onDestroy() {
        super.onDestroy()
        notificationViewModel.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "NOTIFICATION DETAIL"
        supportActionBar?.elevation = 0f

        notificationViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLNotificationViewModel::class.java)

        intent.getParcelableExtra<Notification>("notification-detail")?.let { notification ->
            heading?.text = notification.subject
            message.text = notification.message

            if (!notification.isRead!!) markAsRead(notification.id)
        }
    }

    private fun markAsRead(id:Int=0){
        notificationViewModel.markAsRead(id){isTokenValid,isError,message, response->
            if(!isTokenValid){
                showToast(message)
                navigateTo<LoginActivity>(true)
            }else{
                if(!isError){
                    response?.let {res->

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
