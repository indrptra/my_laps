package id.indosat.ml.base

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.android.AndroidInjection
import id.indosat.ml.R
import id.indosat.ml.viewmodel.MLGeneralViewModel
import javax.inject.Inject
import de.greenrobot.event.EventBus

data class HaloEvent(val teks: String)


class FCMService: FirebaseMessagingService() {

    @Inject
    lateinit var vm : MLGeneralViewModel
    val bus: EventBus = EventBus.getDefault()

    override fun onCreate() {
        AndroidInjection.inject(this)
//        bus.register(this)
//        EventBus.getDefault().post(new NotificationEvent(body));
        super.onCreate()
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        Log.d(TAG, "From: ${remoteMessage?.from}")

        var title: String
        var body: String
        var type: String
        var id: Int?
        var clickAction: String
        var url: String?

        // Check if message contains a data payload.
        remoteMessage?.data!!.isNotEmpty()!!.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            title = remoteMessage.data!!["title"]!!.toString()
            body = remoteMessage.data!!["body"]!!.toString()
            type = remoteMessage.data!!["type"]!!.toString()
            id = remoteMessage.data!!["id"]!!.toInt()
            url = remoteMessage.data!!["url"]
            clickAction =  remoteMessage.data!!["type"]!!.toString()
        }
        //Kirim HaloEvent kepada komponen lain yang subscribe (KelasB)
        bus.post(HaloEvent(body))
        if (clickAction.isNullOrEmpty()) clickAction = remoteMessage?.notification?.clickAction!!

        sendNotification(title, body, clickAction, type, id, url)
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String?) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        vm.registerDeviveId(token!!){_, _, _, _->}
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageTitle: String, messageBody: String, click_action: String, type: String, id: Int?, url: String?) {

        val intent = Intent(click_action)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        if (type=="assigned_course")
            intent.putExtra("course-detail-id", id)

        if (type=="reply_discussion")
            intent.putExtra("forum_detail_id", id)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = 1
        val channelId = "channel-01"
        val channelName = "Channel Name"
        val importance = NotificationManager.IMPORTANCE_HIGH

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelId, channelName, importance
            )

            assert(notificationManager != null)
            notificationManager.createNotificationChannel(mChannel)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val mBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            .setContentIntent(pendingIntent)

        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(intent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
    }

    companion object {

        private const val TAG = "FCMService"
    }
}