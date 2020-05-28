package id.indosat.ml.common.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class MLForumListCommentsResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultForumComment?=null,
    val validtoken: Boolean
)

data class ResultForumComment(
    val endindex: Int,
    val numfound: Int,
    val rows: List<RowForumComment>,
    val startindex: Int
)

data class RowForumComment(
    val attachment: Boolean,
    val attachments: List<RowFCMessageFiles>,
    val created: String,
    val discussionid: Int,
    val id: Int,
    val message: String,
    val messageinlinefiles: List<RowFCMessageFiles>,
    val modified: String,
    val postby: String,
    val postprofileimage: String,
    val subject: String,
    val userid: Int,
    val can_delete: Boolean,
    val can_edit: Boolean,
    val is_liked: Boolean,
    var total_like: Int
)

@Parcelize
data class ReportDiscussion(
    val discussionid: Int,
    val id: Int,
    val message: String,
    val subject: String
): Parcelable


data class RowFCMessageFiles(
    val filename: String,
    val filepath: String,
    val filesize: Int,
    val fileurl: String,
    val isexternalfile: Boolean,
    val mimetype: String,
    val timemodified: Int
)