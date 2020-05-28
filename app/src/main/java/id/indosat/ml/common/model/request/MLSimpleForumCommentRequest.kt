package id.indosat.ml.common.model.request

data class MLSImpleForumCommentRequest(
    val message: String,
    val postid: Int,
    val subject: String,
    val token: String
)