package id.indosat.ml.common.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class MLNotificationResponse(val error: Boolean, val code: Number?, val validtoken: Boolean, val message: String, val result: NotificationResult?)

data class NotificationResult(val numfound: Int?, val startindex: Int?, val endindex: Int?, val rows: List<Notification>?)

@Parcelize
data class Notification(val id: Int, val created: String?, val updated: String?, val subject: String?, val message: String?, var isRead: Boolean?): Parcelable

data class MLNotificationReadResponse(val error: Boolean, val code: Int, val validtoken: Boolean, val message: String, val result: NotificationReadResult?)

data class NotificationReadResult(val id: Number?, val updated: String?)